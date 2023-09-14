package com.convio.client.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TotalNoOfRecords {

	private Client totalNoOfRecords;
	private List<Client> clientList;

}
