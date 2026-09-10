package com.lectures.memo.app.dto;

import com.lectures.memo.app.model.MemoCategory;

public record CategoryCount(MemoCategory category, long total) {
}
