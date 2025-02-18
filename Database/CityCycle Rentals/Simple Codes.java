Simple Codes

Back Button
    xml Code

    <Button
    android:id="@+id/btnBack"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text=" Back"
    android:layout_marginLeft="10dp"
    android:drawableLeft="@drawable/back_icon"
    android:background="?android:attr/selectableItemBackground"
    android:layout_gravity="start"
    android:padding="10dp"
    android:textColor="@color/black"
    android:textSize="16sp"/>

    java code 

    Button btnBack = findViewById(R.id.btnBack);
    btnBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // Navigate back to the previous screen or ProfileActivity
        onBackPressed();  // This is for going back to the previous screen
    }
    });
