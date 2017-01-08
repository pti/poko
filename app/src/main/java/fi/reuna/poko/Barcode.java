package fi.reuna.poko;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

enum BarcodeType {

    Code39(39);

    private final int value;

    BarcodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BarcodeType fromInt(int value) {

        for (BarcodeType bt : BarcodeType.values()) {

            if (bt.value == value) {
                return bt;
            }
        }

        return null;
    }
}

public class Barcode implements Parcelable {

    private String name;
    private BarcodeType barcodeType;
    private String code;
    private Date created;

    public Barcode() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BarcodeType getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(BarcodeType barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Barcode b = (Barcode) o;
        return barcodeType == b.barcodeType && Objects.equals(code, b.code) && Objects.equals(created, b.created) && Objects.equals(name, b.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (barcodeType != null ? barcodeType.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(barcodeType.getValue());
        dest.writeString(code);
        dest.writeLong(created.getTime());
    }

    public static final Parcelable.Creator<Barcode> CREATOR = new Parcelable.Creator<Barcode>() {
        public Barcode createFromParcel(Parcel in) {
            return new Barcode(in);
        }

        public Barcode[] newArray(int size) {
            return new Barcode[size];
        }
    };

    Barcode(Parcel in) {
        name = in.readString();
        barcodeType = BarcodeType.fromInt(in.readInt());
        code = in.readString();
        created = new Date(in.readLong());
    }
}
