package com.xman.admin.modules.member.form;

import lombok.Data;

@Data
public class SearchForm {
    private String searchMbrId;
	private String searchDisposition;
	private String searchDstNum;
	private String searchAllCheck;	//미사용포함
	private String managerYn;		//관리자 여부
	private String searchMm;
}
