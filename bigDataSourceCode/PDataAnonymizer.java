import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDataAnonymizer {
    public static void main(String[] args) {
        String inputFilePath = "PatientNotes.txt";
        String anonymizedDataFilePath = "AnonymizedPNotes.txt";
        String mappingFilePath = "MappingDocument.txt";

        // Patterns to identify names and addresses // 
        String fullNamePattern = "\\b(?:Ms\\.?|Mr\\.?|Mrs\\.?|Dr\\.?)\\s+[A-Z][a-z]+(?:\\s+[A-Z][a-zA-Z]+)?(?:['’]?s)?|Mr\\.\\s+[A-Z][a-z]+(?=,)";
        String agePattern = "\\b(?:\\d+-year-old|aged\\s\\d+)";
        String addressPattern = "\\d+\\s[A-Za-z\\s]+,\\s[A-Za-z\\s]+,\\s[A-Z]{2},|\\b[A-Za-z]+\\s[A-Za-z]+,\\s[A-Z]{2}\\b";
        String firstNamePattern = "\\b[A-Za-z]+\\b";  

        int patientId = 1;
        HashMap<String, String> patientMapping = new HashMap<>();
        HashMap<String, String> fullNameMapping = new HashMap<>();
        HashMap<String, String> firstNameMapping = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             PrintWriter anonymizedWriter = new PrintWriter(new FileWriter(anonymizedDataFilePath));
             PrintWriter mappingWriter = new PrintWriter(new FileWriter(mappingFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Matcher fullNameMatcher = Pattern.compile(fullNamePattern).matcher(line);
                while (fullNameMatcher.find()) {
                    String fullName = fullNameMatcher.group();
                    String[] parts = fullName.split(" ");
                    String firstName = parts[1];

                    String patientIdStr = "1." + patientId;
                    line = line.replaceFirst(Pattern.quote(fullName), patientIdStr);

                    fullNameMapping.put(fullName, patientIdStr);
                    firstNameMapping.put(firstName, patientIdStr);

                    patientMapping.put(patientIdStr, "Name: " + fullName);
                    patientId++;
                }

                Matcher addressMatcher = Pattern.compile(addressPattern).matcher(line);
                while (addressMatcher.find()) {
                    String address = addressMatcher.group();
                    String patientIdStr = "2." + patientId;
                    line = line.replaceFirst(Pattern.quote(address), patientIdStr);

                    patientMapping.put(patientIdStr, "Address: " + address);
                    patientId++;
                }
                Matcher ageMatcher = Pattern.compile(agePattern).matcher(line);
                while (ageMatcher.find()) {
                    String age = ageMatcher.group();
                    String patientIdStr = "3." + patientId;
                    line = line.replaceFirst(Pattern.quote(age), patientIdStr);

                    patientMapping.put(patientIdStr, "age: " + age);
                    patientId++;
                }

                Matcher firstNameMatcher = Pattern.compile(firstNamePattern).matcher(line);
                StringBuffer sb = new StringBuffer();
                while (firstNameMatcher.find()) {
                    String firstName = firstNameMatcher.group();
                    if (firstNameMapping.containsKey(firstName)) {
                        String patientIdStr = firstNameMapping.get(firstName);
                        firstNameMatcher.appendReplacement(sb, patientIdStr);
                    }
                }
                firstNameMatcher.appendTail(sb);
                line = sb.toString();

                anonymizedWriter.println(line);
            }

            for (String id : patientMapping.keySet()) {
                mappingWriter.println(id + " ---> " + patientMapping.get(id));
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
