package net.bluemix.seek.model;

public class TrainAlbumResponse {

	private String entryid;
	private String album;
	private int image_count;
	private boolean rebuild;
	private String error;
	
	public String getEntryid() {
		return entryid;
	}
	public void setEntryid(String entryid) {
		this.entryid = entryid;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public int getImage_count() {
		return image_count;
	}
	public void setImage_count(int image_count) {
		this.image_count = image_count;
	}
	public boolean isRebuild() {
		return rebuild;
	}
	public void setRebuild(boolean rebuild) {
		this.rebuild = rebuild;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public String toString() {
		return "TrainAlbumResponse [entryid=" + entryid + ", album=" + album
				+ ", image_count=" + image_count + ", rebuild=" + rebuild
				+ ", error=" + error + "]";
	}
}
