package com.example.backend.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    private static final List<String> SKILL_DICTIONARY = Arrays.asList(
        "java", "python", "javascript", "typescript", "c++", "c#", "go",
        "kotlin", "swift", "ruby", "php", "scala", "dart", "bash", "rust",
        "react", "angular", "vue", "html", "css", "sass", "tailwind",
        "next.js", "gatsby", "redux", "webpack", "vite", "figma", "bootstrap",
        "node.js", "spring", "spring boot", "django", "flask", "fastapi",
        "express", "laravel", "rails", "graphql", "rest api", "asp.net",
        "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
        "oracle", "sqlite", "cassandra", "dynamodb", "firebase", "sql",
        "aws", "gcp", "azure", "docker", "kubernetes", "jenkins",
        "terraform", "ansible", "linux", "git", "github", "gitlab", "ci/cd",
        "machine learning", "deep learning", "tensorflow", "pytorch",
        "pandas", "numpy", "scikit-learn", "tableau", "power bi",
        "data analysis", "nlp", "computer vision",
        "android", "ios", "react native", "flutter",
        "agile", "scrum", "jira", "microservices", "kafka", "rabbitmq",
        "web development", "cloud computing", "data structures", "algorithms"
    );

    private static final Map<String, Pattern> PATTERNS = new LinkedHashMap<>();

    static {
        for (String skill : SKILL_DICTIONARY) {
            String escaped = skill
                .replace(".", "\\.")
                .replace("+", "\\+")
                .replace("#", "\\#")
                .replace("/", "\\/");
            String regex = "(?i)(?<![a-zA-Z0-9])" + escaped + "(?![a-zA-Z0-9])";
            PATTERNS.put(skill, Pattern.compile(regex));
        }
    }

    // Tesseract data paths to try (covers Windows, Mac, Linux, Render)
    private static final String[] TESS_DATA_PATHS = {
        "/usr/share/tesseract-ocr/4.00/tessdata",
        "/usr/share/tesseract-ocr/5/tessdata",
        "/usr/share/tessdata",
        "/usr/local/share/tessdata",
        "C:/Program Files/Tesseract-OCR/tessdata",
        "C:/Program Files (x86)/Tesseract-OCR/tessdata"
    };

    public Map<String, Object> extractSkillsWithInfo(MultipartFile file) throws IOException {
        // Step 1: Try normal text extraction
        String text = extractTextFromPdf(file);
        String cleaned = text.trim().replaceAll("\\s+", " ");
        boolean isImageBased = cleaned.length() < 50;

        // Step 2: If image-based, try OCR — but don't crash if Tesseract isn't installed
        if (isImageBased) {
            System.out.println("Image-based PDF detected. Attempting OCR...");
            try {
                String ocrText = extractTextWithOCR(file);
                if (ocrText != null && !ocrText.isBlank()) {
                    text = ocrText;
                }
            } catch (Exception e) {
                System.err.println("OCR unavailable, skipping: " + e.getMessage());
                // Continue with empty text — will return fallback skills below
            }
        }

        List<String> skills = matchSkills(text);

        Map<String, Object> result = new HashMap<>();
        result.put("skills", skills.isEmpty() ? List.of("General Programming") : skills);
        result.put("isImageBased", isImageBased);
        result.put("message", skills.isEmpty()
            ? "No recognizable skills found. Try updating your profile manually."
            : "Skills extracted successfully!");

        return result;
    }

    public List<String> extractSkills(MultipartFile file) throws IOException {
        Map<String, Object> result = extractSkillsWithInfo(file);
        return (List<String>) result.get("skills");
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextWithOCR(MultipartFile file) throws IOException {
        StringBuilder ocrText = new StringBuilder();
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFRenderer renderer = new PDFRenderer(document);
            Tesseract tesseract = new Tesseract();

            // Find a valid tessdata path
            String validPath = null;
            for (String path : TESS_DATA_PATHS) {
                if (new java.io.File(path).exists()) {
                    validPath = path;
                    break;
                }
            }
            if (validPath == null) {
                throw new RuntimeException("Tesseract not installed or tessdata not found.");
            }

            tesseract.setDatapath(validPath);
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
                try {
                    ocrText.append(tesseract.doOCR(image)).append("\n");
                } catch (TesseractException e) {
                    System.err.println("OCR failed on page " + i + ": " + e.getMessage());
                }
            }
        }
        return ocrText.toString();
    }

    private List<String> matchSkills(String text) {
        List<String> matched = new ArrayList<>();
        for (Map.Entry<String, Pattern> entry : PATTERNS.entrySet()) {
            if (entry.getValue().matcher(text).find()) {
                matched.add(formatSkill(entry.getKey()));
            }
        }
        Collections.sort(matched);
        return matched;
    }

    private String formatSkill(String skill) {
        Map<String, String> display = new HashMap<>();
        display.put("java", "Java"); display.put("python", "Python");
        display.put("javascript", "JavaScript"); display.put("typescript", "TypeScript");
        display.put("c++", "C++"); display.put("c#", "C#"); display.put("go", "Go");
        display.put("kotlin", "Kotlin"); display.put("swift", "Swift");
        display.put("ruby", "Ruby"); display.put("php", "PHP");
        display.put("scala", "Scala"); display.put("dart", "Dart");
        display.put("bash", "Bash"); display.put("rust", "Rust");
        display.put("react", "React"); display.put("angular", "Angular");
        display.put("vue", "Vue"); display.put("html", "HTML");
        display.put("css", "CSS"); display.put("sass", "Sass");
        display.put("tailwind", "Tailwind"); display.put("next.js", "Next.js");
        display.put("gatsby", "Gatsby"); display.put("redux", "Redux");
        display.put("webpack", "Webpack"); display.put("vite", "Vite");
        display.put("figma", "Figma"); display.put("bootstrap", "Bootstrap");
        display.put("node.js", "Node.js"); display.put("spring", "Spring");
        display.put("spring boot", "Spring Boot"); display.put("django", "Django");
        display.put("flask", "Flask"); display.put("fastapi", "FastAPI");
        display.put("express", "Express"); display.put("laravel", "Laravel");
        display.put("rails", "Rails"); display.put("graphql", "GraphQL");
        display.put("rest api", "REST API"); display.put("asp.net", "ASP.NET");
        display.put("mysql", "MySQL"); display.put("postgresql", "PostgreSQL");
        display.put("mongodb", "MongoDB"); display.put("redis", "Redis");
        display.put("elasticsearch", "Elasticsearch"); display.put("oracle", "Oracle");
        display.put("sqlite", "SQLite"); display.put("cassandra", "Cassandra");
        display.put("dynamodb", "DynamoDB"); display.put("firebase", "Firebase");
        display.put("sql", "SQL"); display.put("aws", "AWS");
        display.put("gcp", "GCP"); display.put("azure", "Azure");
        display.put("docker", "Docker"); display.put("kubernetes", "Kubernetes");
        display.put("jenkins", "Jenkins"); display.put("terraform", "Terraform");
        display.put("ansible", "Ansible"); display.put("linux", "Linux");
        display.put("git", "Git"); display.put("github", "GitHub");
        display.put("gitlab", "GitLab"); display.put("ci/cd", "CI/CD");
        display.put("machine learning", "Machine Learning");
        display.put("deep learning", "Deep Learning");
        display.put("tensorflow", "TensorFlow"); display.put("pytorch", "PyTorch");
        display.put("pandas", "Pandas"); display.put("numpy", "NumPy");
        display.put("scikit-learn", "Scikit-learn"); display.put("tableau", "Tableau");
        display.put("power bi", "Power BI"); display.put("data analysis", "Data Analysis");
        display.put("nlp", "NLP"); display.put("computer vision", "Computer Vision");
        display.put("android", "Android"); display.put("ios", "iOS");
        display.put("react native", "React Native"); display.put("flutter", "Flutter");
        display.put("agile", "Agile"); display.put("scrum", "Scrum");
        display.put("jira", "Jira"); display.put("microservices", "Microservices");
        display.put("kafka", "Kafka"); display.put("rabbitmq", "RabbitMQ");
        display.put("web development", "Web Development");
        display.put("cloud computing", "Cloud Computing");
        display.put("data structures", "Data Structures");
        display.put("algorithms", "Algorithms");
        return display.getOrDefault(skill,
            Character.toUpperCase(skill.charAt(0)) + skill.substring(1));
    }
}
