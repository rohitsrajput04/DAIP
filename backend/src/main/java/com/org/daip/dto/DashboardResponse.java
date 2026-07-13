package com.db.daip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalDocuments;
    private long totalChatSessions;
    private long totalUsers;
    private long documentsUploadedToday;
    private String platformStatus;
    private String activeDomain;
}
