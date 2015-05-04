package instant;

import datetime.DateTimeParserBucket;

public interface InternalParser {
    int estimateParsedLength();
    int parseInto(DateTimeParserBucket bucket, CharSequence text, int position);
}
