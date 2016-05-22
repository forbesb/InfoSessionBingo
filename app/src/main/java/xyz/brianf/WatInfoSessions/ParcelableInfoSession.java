package xyz.brianf.WatInfoSessions;

import android.os.Parcel;
import android.os.Parcelable;

import Resources.InfoSession;

/**
 * Created by brian on 16/05/16.
 */
public class ParcelableInfoSession implements Parcelable {

    private InfoSession data;

    protected ParcelableInfoSession(Parcel in) {
        data = new InfoSession();
        data.setAudience(in.readString());
        //data.setBuilding(in.readParcelable(Building.class));
        data.setDate(in.readString());
        data.setDescription(in.readString());
        data.setEmployer(in.readString());
        data.setEnd_time(in.readString());
        data.setStart_time(in.readString());
        data.setId(in.readInt());
        data.setLink(in.readString());
        data.setLocation(in.readString());
        data.setPrograms(in.readString());
        data.setWebsite(in.readString());
    }

    public ParcelableInfoSession(InfoSession i){
        data = i;
    }

    public static final Creator<ParcelableInfoSession> CREATOR = new Creator<ParcelableInfoSession>() {
        @Override
        public ParcelableInfoSession createFromParcel(Parcel in) {
            return new ParcelableInfoSession(in);
        }

        @Override
        public ParcelableInfoSession[] newArray(int size) {
            return new ParcelableInfoSession[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data.getAudience());
        //dest.writeParcelable(data.getBuilding());
        dest.writeString(data.getDate());
        dest.writeString(data.getDescription());
        dest.writeString(data.getEmployer());
        dest.writeString(data.getEnd_time());
        dest.writeString(data.getStart_time());
        dest.writeInt(data.getId());
        dest.writeString(data.getLink());
        dest.writeString(data.getLocation());
        dest.writeString(data.getPrograms());
        dest.writeString(data.getWebsite());

    }

    public InfoSession getData() {
        return data;
    }

    public void setData(InfoSession data) {
        this.data = data;
    }
}
