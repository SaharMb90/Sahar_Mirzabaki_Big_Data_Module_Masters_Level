import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testingAnonymization {
    public static void main(String[] args) {
        String anonymizedDataFilePath = "AnonymizedPNotes.txt";
        
        // Read back the anonymized data for validation
        try (BufferedReader reader = new BufferedReader(new FileReader(anonymizedDataFilePath))) {
            StringBuilder anonymizedContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                anonymizedContent.append(line).append(System.lineSeparator());
            }
            // Validate the anonymized content
            if (!validateAnonymization(anonymizedContent.toString())) {
                System.err.println("Validation failed: Unintended data residues detected.");
            } else {
                System.out.println("Validation passed: No unintended data residues detected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean validateAnonymization(String anonymizedText) {
        // Patterns that should NOT appear in successfully anonymized text
        String[] validationPatterns = {
            "\\\\b(?:Ms\\\\.?|Mr\\\\.?|Mrs\\\\.?|Dr\\\\.?)\\\\s+[A-Z][a-z]+(?:\\\\s+[A-Z][a-zA-Z]+)?(?:['’]?s)?|Mr\\\\.\\\\s+[A-Z][a-z]+(?=,)",
            "\\\\b(?:\\\\d+-year-old|aged\\\\s\\\\d+)",
            "\\\\d+\\\\s[A-Za-z\\\\s]+,\\\\s[A-Za-z\\\\s]+,\\\\s[A-Z]{2},|\\\\b[A-Za-z]+\\\\s[A-Za-z]+,\\\\s[A-Z]{2}\\\\b"
        };
        
        for (String pattern : validationPatterns) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(anonymizedText);
            if (m.find()) {
                // If any pattern matches, validation fails
                return false;
            }
        }
        // No patterns found, validation successful
        return true;
    }
}
