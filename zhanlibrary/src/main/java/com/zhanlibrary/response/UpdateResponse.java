package com.zhanlibrary.response;


import com.zhanlibrary.model.UpdateModel;

public class UpdateResponse extends BaseResponse {
	private UpdateModel data;

	public UpdateModel getData() {
		return data;
	}

	public void setData(UpdateModel data) {
		this.data = data;
	}
	

}
