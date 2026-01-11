package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MethodParameter;

/**
 * Parser for message syntax in sequence diagrams.
 * Supports syntax: [returnValue [: returnType] :=] messageName([parameter1 [: type1] [, parameter2 [: type2], ...]])
 *
 * @author Dimitris Dranidis
 */
public class MessageSyntaxParser {

    // Pattern to match the complete message syntax
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
        "^\\s*(?:([\\w]+)\\s*(?::\\s*([\\w]+))?\\s*:=\\s*)?([\\w]+)\\s*\\(([^)]*)\\)\\s*$"
    );

    // Pattern to match individual parameters
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(
        "([\\w]+)\\s*(?::\\s*([\\w]+))?"
    );

    /**
     * Parse a message syntax string.
     *
     * @param text The text to parse
     * @return ParseResult containing the parsed components or error information
     */
    public ParseResult parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ParseResult("Input text cannot be empty");
        }

        Matcher matcher = MESSAGE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return new ParseResult("Invalid message syntax. Expected format: [returnValue [: returnType] :=] messageName([param1 [: type1], ...])");
        }

        String returnValue = matcher.group(1);  // Optional return value
        String returnType = matcher.group(2);   // Optional return type
        String messageName = matcher.group(3);  // Required message name
        String parametersString = matcher.group(4);  // Parameters string (may be empty)

        if (messageName == null || messageName.trim().isEmpty()) {
            return new ParseResult("Message name cannot be empty");
        }

        // Parse parameters
        List<ParameterInfo> parameters = new ArrayList<>();
        if (parametersString != null && !parametersString.trim().isEmpty()) {
            String[] paramTokens = parametersString.split(",");
            for (String paramToken : paramTokens) {
                paramToken = paramToken.trim();
                if (!paramToken.isEmpty()) {
                    Matcher paramMatcher = PARAMETER_PATTERN.matcher(paramToken);
                    if (paramMatcher.matches()) {
                        String paramName = paramMatcher.group(1);
                        String paramType = paramMatcher.group(2);
                        parameters.add(new ParameterInfo(paramName, paramType));
                    } else {
                        return new ParseResult("Invalid parameter syntax: " + paramToken);
                    }
                }
            }
        }

        return new ParseResult(returnValue, returnType, messageName, parameters);
    }

    /**
     * Reconstruct the message syntax string from a CallMessage.
     *
     * @param message The CallMessage to reconstruct
     * @return The reconstructed syntax string
     */
    public String reconstruct(CallMessage message) {
        if (message == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // Add return value and type if present
        MessageReturnValue returnValue = message.getReturnValue();
        if (returnValue != null && returnValue.getName() != null && !returnValue.getName().isEmpty()) {
            sb.append(returnValue.getName());

            // Add return type if present
            if (message.getReturnType() != null) {
                sb.append(": ").append(message.getReturnType().getName());
            }

            sb.append(" := ");
        }

        // Add message name
        String messageName = message.getName();
        if (messageName == null) {
            messageName = "";
        }
        sb.append(messageName);

        // Add parameters
        sb.append("(");
        List<MethodParameter> parameters = message.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                MethodParameter param = parameters.get(i);
                sb.append(param.getName());
                if (param.getType() != null) {
                    sb.append(": ").append(param.getType().getName());
                }
            }
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * Result of parsing a message syntax string.
     */
    public static class ParseResult {
        private final boolean valid;
        private final String errorMessage;
        private final String returnValue;
        private final String returnType;
        private final String messageName;
        private final List<ParameterInfo> parameters;

        // Constructor for successful parse
        public ParseResult(String returnValue, String returnType, String messageName, List<ParameterInfo> parameters) {
            this.valid = true;
            this.errorMessage = null;
            this.returnValue = returnValue;
            this.returnType = returnType;
            this.messageName = messageName;
            this.parameters = parameters != null ? parameters : new ArrayList<>();
        }

        // Constructor for failed parse
        public ParseResult(String errorMessage) {
            this.valid = false;
            this.errorMessage = errorMessage;
            this.returnValue = null;
            this.returnType = null;
            this.messageName = null;
            this.parameters = new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getReturnValue() {
            return returnValue;
        }

        public String getReturnType() {
            return returnType;
        }

        public String getMessageName() {
            return messageName;
        }

        public List<ParameterInfo> getParameters() {
            return parameters;
        }
    }

    /**
     * Information about a parsed parameter.
     */
    public static class ParameterInfo {
        private final String name;
        private final String type;

        public ParameterInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
