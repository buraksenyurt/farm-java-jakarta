package com.lectures.memo.app.service.dto;

import com.lectures.memo.app.model.MemoCategory;

public record CategoryCount(MemoCategory category, long total) {
}
