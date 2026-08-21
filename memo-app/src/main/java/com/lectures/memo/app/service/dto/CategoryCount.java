package com.lectures.memo.app.service.dto;

import com.lectures.memo.app.entity.MemoCategory;

public record CategoryCount(MemoCategory category, long total) {
}
