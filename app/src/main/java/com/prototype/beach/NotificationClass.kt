package com.prototype.beach

import android.app.Notification
import android.os.Parcel
import android.os.Parcelable

class NotificationClass(
    var id: Int = -1,
    var type: Int = -1,
    var title: String = "NULL title",
    var message: String = "NULL message",
    var mainImageID: Int = -1,
    var colorCode: String = "NULL Color Code",
    var isDeleted: Boolean = false,
) : Notification(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "NULL title",
        parcel.readString() ?: "NULL message",
        parcel.readInt(),
        parcel.readString() ?: "NULL Color Code",
        parcel.readBoolean() ?: true,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(type)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeInt(mainImageID)
        parcel.writeString(colorCode)
        parcel.writeBoolean(isDeleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<com.prototype.beach.NotificationClass> {
        override fun createFromParcel(parcel: Parcel): com.prototype.beach.NotificationClass {
            return com.prototype.beach.NotificationClass(parcel)
        }

        override fun newArray(size: Int): Array<com.prototype.beach.NotificationClass?> {
            return arrayOfNulls(size)
        }
    }
}
