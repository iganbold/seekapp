package net.bluemix.seek.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PhotoModel implements Serializable {
	
	@SerializedName("_id")
	private String entryId;
	private List<String> urls;
	private String tag;
	private String time;
	private String note;
	private Geometry geo;
	
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Geometry getGeo() {
		return geo;
	}

	public void setGeo(Geometry geo) {
		this.geo = geo;
	}

	@Override
	public String toString() {
		return "PhotoModel{" +
				"entryId='" + entryId + '\'' +
				", urls=" + urls +
				", tag='" + tag + '\'' +
				", time='" + time + '\'' +
				", note='" + note + '\'' +
				", geo=" + geo +
				'}';
	}
}
