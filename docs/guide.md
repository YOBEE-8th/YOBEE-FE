# 가이드 화면

## 결과물

<image width = 500 src="https://user-images.githubusercontent.com/49333608/230719413-13007ca8-1617-4ae1-bdda-2e3df454dea6.gif">

<br>

## 구현 내용

    - 음성 비서 사용법 가이드 화면 구현

<br>
<br>

# 1. FrameLayout

```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <include
        layout="@layout/layout_guide_base"/>
    <View
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#E0000000"/>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TextView
            android:id="@+id/tv_no_name_command_description"
            style="@style/TextViewBold.Size22"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="20dp"
            android:text="@string/title_no_name_command_description"
            android:textColor="@color/white"
            android:gravity="center"
            app:layout_constraintBottom_toTopOf="@+id/tv_no_name_command_example"
            app:layout_constraintEnd_toEndOf="@+id/iv_no_name_command" />

        <TextView
            android:id="@+id/tv_no_name_command_example"
            style="@style/TextViewBold.Size18"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="20dp"
            android:text="@string/title_no_name_command_example"
            android:textColor="@color/white"
            app:layout_constraintBottom_toTopOf="@+id/iv_no_name_command"
            app:layout_constraintEnd_toEndOf="@+id/tv_no_name_command_description"
            app:layout_constraintStart_toStartOf="@+id/tv_no_name_command_description" />

        <ImageView
            android:id="@+id/iv_no_name_command"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:layout_marginEnd="12dp"
            android:layout_marginBottom="20dp"
            android:src="@drawable/ic_guide_arrow_down"
            app:layout_constraintBottom_toBottomOf="@+id/floatingActionButton"
            app:layout_constraintEnd_toStartOf="@+id/floatingActionButton" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/floatingActionButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginEnd="30dp"
            android:layout_marginBottom="20dp"
            android:src="@drawable/ic_mic"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</FrameLayout>
```

- FrameLayout으로 맨 아래에는 소개화면 UI, 그 위에는 배경, 그 위에는 가이드 내용을 작성
