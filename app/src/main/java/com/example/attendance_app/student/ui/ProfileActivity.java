package com.example.attendance_app.student.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImage;
import com.example.attendance_app.MainActivity;
import com.example.attendance_app.R;
import com.example.attendance_app.student.fragments.MarkAttendanceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mStudentDatabase;
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private CircleImageView mImage;
    private TextView mName, mRollNumber, mClass;
    private Button ImageBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private static final int GALLERY_PICK = 1;
private ImageView logout;
    private StorageReference mImageStorage;

    protected String downloadUrl, thumb_downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImage = findViewById(R.id.profile_image);
        logout=findViewById(R.id.imageView);
        mName = findViewById(R.id.name_studentProfile);
        ImageBtn = findViewById(R.id.change_Image);
        mRollNumber = findViewById(R.id.rollNumber_studentProfile);
        mClass = findViewById(R.id.class_studentProfile);
        mAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(this);
        mProgress = new ProgressDialog(this);

        String uid = mCurrentUser.getUid();

        mStudentDatabase = FirebaseDatabase.getInstance().getReference().child("Students");
        mStudentDatabase.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mStudentDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.getKey().toString().contentEquals(uid)) {
                    String name = snapshot.child("name").getValue().toString();
                    final String image = snapshot.child("image").getValue().toString();
                    String roll = snapshot.child("roll_number").getValue().toString();
                    String classRoom = snapshot.child("class_room").getValue().toString();

                    mName.setText(name);
                    mRollNumber.setText(roll);
                    mClass.setText("Class: " + classRoom);

                    if (!image.equals("default")) {
                        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.defaultprofile).into(mImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.defaultprofile).into(mImage);
                            }
                        });
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/+");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("Please wait while we upload and process image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                final Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                String currentUserID = mCurrentUser.getUid().toString();

//                Bitmap thumb_bitmap = new Compressor(ProfileActivity.this)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(75)
//                        .compressToBitmap(thumb_filePath);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mImageStorage.child("profile_images").child(currentUserID + ".jpg");
                //final StorageReference thumb_firebaseFilepath = mImageStorage.child("profile_images").child("thumbs").child(currentUserID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    Toast.makeText(ProfileActivity.this, "Upload success! URL - " + downloadUrl, Toast.LENGTH_SHORT).show();
                                    //Map update_hashMap = new HashMap<>();
                                    //update_hashMap.put("image", downloadUrl);
                                    //update_hashMap.put("thumb_image", thumb_downloadUrl);

                                    mStudentDatabase.child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProgress.dismiss();
                                                String image = downloadUrl;
                                                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                                        .placeholder(R.drawable.defaultprofile).into(mImage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(image).placeholder(R.drawable.defaultprofile).into(mImage);
                                                    }
                                                });
                                                Toast.makeText(ProfileActivity.this, "Uploading Successful", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Error in uploading!", Toast.LENGTH_LONG).show();
                                                mProgress.dismiss();
                                            }
                                        }
                                    });

                                }
                            });

//                            thumb_firebaseFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    thumb_downloadUrl = uri.toString();
//                                    Toast.makeText(ProfileActivity.this, "Upload success! Thumb URL - " + thumb_downloadUrl, Toast.LENGTH_LONG).show();
//                                }
//                            });

                            //                          UploadTask uploadTask = thumb_firebaseFilepath.putBytes(thumb_byte);

//                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//                                    if (thumb_task.isSuccessful()) {
//
//                                    } else {
//                                        Toast.makeText(ProfileActivity.this, "Error in uploading thumbnail", Toast.LENGTH_LONG).show();
//                                        mProgress.dismiss();
//                                    }
//
//                                }
//                            });

                        } else {
                            Toast.makeText(ProfileActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, StudentHomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView) {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }


    }
}