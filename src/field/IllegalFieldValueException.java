package field;

import datetime.DateTimeFieldType;
import duration.DurationFieldType;

public class IllegalFieldValueException extends IllegalArgumentException {
    private static final long serialVersionUID = 6305711765985447737L;
    private String iMessage;

    public IllegalFieldValueException(DateTimeFieldType fieldType, Number value, Number lowerBound, Number upperBound) {
        super(createMessage(fieldType.getName(), value, lowerBound, upperBound, null));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(DateTimeFieldType fieldType, Number value, String explain) {
        super(createMessage(fieldType.getName(), value, null, null, explain));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(DurationFieldType fieldType, Number value, Number lowerBound, Number upperBound) {
        super(createMessage(fieldType.getName(), value, lowerBound, upperBound, null));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(String fieldName, Number value, Number lowerBound, Number upperBound) {
        super(createMessage(fieldName, value, lowerBound, upperBound, null));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(DateTimeFieldType fieldType, String value) {
        super(createMessage(fieldType.getName(), value));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(DurationFieldType fieldType, String value) {
        super(createMessage(fieldType.getName(), value));
        iMessage = super.getMessage();
    }
    public IllegalFieldValueException(String fieldName, String value) {
        super(createMessage(fieldName, value));
        iMessage = super.getMessage();
    }

    private static String createMessage(String fieldName, Number value, Number lowerBound, Number upperBound, String explain) {
        StringBuilder buf = new StringBuilder().append("Value ").append(value).append(" for ").append(fieldName).append(' ');

        if (lowerBound == null)
        {
            if (upperBound == null)
            {
                buf.append("is not supported");
            }
            else
            {
                buf.append("must not be larger than ").append(upperBound);
            }
        }
        else if (upperBound == null)
        {
            buf.append("must not be smaller than ").append(lowerBound);
        }
        else
        {
            buf.append("must be in the range [")
                    .append(lowerBound)
                    .append(',')
                    .append(upperBound)
                    .append(']');
        }

        if (explain != null)
        {
            buf.append(": ").append(explain);
        }

        return buf.toString();
    }
    private static String createMessage(String fieldName, String value) {
        StringBuilder buf = new StringBuilder().append("Value ");

        if (value == null)
        {
            buf.append("null");
        }
        else
        {
            buf.append('"');
            buf.append(value);
            buf.append('"');
        }

        buf.append(" for ").append(fieldName).append(' ').append("is not supported");
        return buf.toString();
    }

    public String getMessage() {
        return iMessage;
    }

    public void prependMessage(String message) {
        if (iMessage == null) {
            iMessage = message;
        } else if (message != null) {
            iMessage = message + ": " + iMessage;
        }
    }
}
