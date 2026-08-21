package com.lectures.memo.app.service.dto;

import java.util.List;

public record MemoStats(long total, List<CategoryCount> byCategory) {
}
