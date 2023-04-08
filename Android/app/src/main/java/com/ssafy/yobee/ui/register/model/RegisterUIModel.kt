package com.ssafy.yobee.ui.register.model

import android.os.Parcel
import android.os.Parcelable
import com.ssafy.domain.model.account.Account

data class RegisterUIModel(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val profileImgUrl: String? = "",
    var verificationCode: String = "",
    var type: Int = 0,
    var emailCheck: Int = 0,
    var passwordCheck: Int = 0,
    var nicknameCheck: Int = 0,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(nickname)
        parcel.writeString(profileImgUrl)
        parcel.writeString(verificationCode)
        parcel.writeInt(type)
        parcel.writeInt(emailCheck)
        parcel.writeInt(passwordCheck)
        parcel.writeInt(nicknameCheck)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegisterUIModel> {
        override fun createFromParcel(parcel: Parcel): RegisterUIModel {
            return RegisterUIModel(parcel)
        }

        override fun newArray(size: Int): Array<RegisterUIModel?> {
            return arrayOfNulls(size)
        }
    }
}

fun Account.toRegisterUIModel() = RegisterUIModel(
    email = this.email,
    password = this.password,
    nickname = this.nickname,
    profileImgUrl = this.profileImgUrl,
    verificationCode = this.verificationCode,
    type = this.type,
    emailCheck = this.emailCheck,
    passwordCheck = this.passwordCheck,
    nicknameCheck = this.nicknameCheck
)