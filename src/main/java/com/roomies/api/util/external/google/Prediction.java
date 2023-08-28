package com.roomies.api.util.external.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Prediction {
    private String description;
    private List<MatchedSubstring> matched_substrings;
    private String place_id;
    private String reference;
    private StructuredFormatting structured_formatting;
    private List<Term> terms;
    private List<String> types;

    static class MatchedSubstring {
        private int length;
        private int offset;

    }

    static class StructuredFormatting {
        private String main_text;
        private List<MatchedSubstring> main_text_matched_substrings;
        private String secondary_text;
    }

    static class Term {
        private int offset;
        private String value;

    }


}
