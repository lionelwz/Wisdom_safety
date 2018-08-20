package com.wf.wisdom_safety.bean.inspect;

import org.json.JSONObject;

/**
 * 描述：广告信息</br>
 * @author Eden Cheng</br>
 * @version 2015年4月23日 上午11:32:53
 */
public class ADInfo {
    String id = "";
	String url = "";
	String content = "";
	String type = "";
	String mTopicId;
	String mTitle;
	String mPlateId;

	public static ADInfo createFromJSON(JSONObject obj){

		try {
			ADInfo item = new ADInfo();
			item.setUrl(obj.optString("coverUrl"));
			item.setPlateId(obj.optString("plateId"));
			item.setId(obj.optString("id"));
			item.setTitle(obj.optString("title"));
			item.setTopicId(obj.optString("topicId"));
			return item;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTopicId() {
		return mTopicId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getPlateId() {
		return mPlateId;
	}

	public void setTopicId(String topicId) {
		mTopicId = topicId;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setPlateId(String plateId) {
		mPlateId = plateId;
	}
}
