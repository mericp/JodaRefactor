package datetime;

public interface DateTimeParser {
    int estimateParsedLength();
    int parseInto(DateTimeParserBucket bucket, String text, int position);
}
