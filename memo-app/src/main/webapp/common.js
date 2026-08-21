const CATEGORY_LABELS = {
    SCIENCE: "Bilim",
    COMMON: "Genel",
    SUSTAINABILITY: "Sürdürülebilirlik",
    WORDS_FROM_BOOKS: "Kitaplardan Alıntılar"
};

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}
