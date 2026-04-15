import io
import re
from collections import Counter
from pathlib import Path
from typing import Iterable

import docx
import pdfplumber
import streamlit as st
from PyPDF2 import PdfReader


st.set_page_config(page_title="Resume Scorer", page_icon="◎", layout="centered")


JOBS = {
    "Software Engineer": ["python", "javascript", "react", "node.js", "sql", "git", "docker", "rest api", "typescript", "html", "css", "java", "algorithms", "data structures", "ci/cd"],
    "Data Scientist": ["python", "machine learning", "sql", "pandas", "numpy", "scikit-learn", "statistics", "data visualization", "tensorflow", "r", "tableau", "nlp", "pytorch", "spark", "feature engineering"],
    "DevOps Engineer": ["docker", "kubernetes", "aws", "terraform", "linux", "bash", "ci/cd", "jenkins", "git", "ansible", "monitoring", "python", "networking", "azure", "gcp"],
    "Frontend Developer": ["react", "javascript", "typescript", "html", "css", "redux", "next.js", "webpack", "figma", "git", "responsive design", "jest", "graphql", "vue.js", "tailwind"],
    "Backend Developer": ["python", "java", "node.js", "sql", "postgresql", "mongodb", "rest api", "docker", "aws", "microservices", "redis", "git", "go", "kafka", "graphql"],
    "Machine Learning Engineer": ["python", "tensorflow", "pytorch", "mlflow", "docker", "kubernetes", "sql", "spark", "scikit-learn", "fastapi", "aws", "data pipelines", "cuda", "feature store", "onnx"],
    "Product Manager": ["roadmapping", "agile", "scrum", "jira", "user research", "sql", "a/b testing", "figma", "stakeholder management", "okrs", "analytics", "prd", "go-to-market", "data analysis", "excel"],
    "UI/UX Designer": ["figma", "adobe xd", "prototyping", "user research", "wireframing", "design systems", "usability testing", "css", "html", "sketch", "accessibility", "illustration", "interaction design", "animation", "photoshop"],
    "Data Analyst": ["sql", "python", "excel", "tableau", "power bi", "statistics", "pandas", "data visualization", "r", "google analytics", "etl", "looker", "snowflake", "reporting", "dashboards"],
    "Android Developer": ["kotlin", "java", "android sdk", "jetpack compose", "mvvm", "rest api", "firebase", "sqlite", "git", "retrofit", "coroutines", "dagger", "junit", "gradle", "material design"],
    "iOS Developer": ["swift", "objective-c", "swiftui", "uikit", "xcode", "rest api", "core data", "git", "firebase", "mvvm", "combine", "cocoapods", "instruments", "testflight", "push notifications"],
    "Cybersecurity Analyst": ["network security", "siem", "penetration testing", "python", "linux", "firewalls", "ids/ips", "cryptography", "incident response", "owasp", "iso 27001", "vulnerability assessment", "splunk", "threat modeling", "compliance"],
    "Cloud Architect": ["aws", "azure", "gcp", "terraform", "docker", "kubernetes", "networking", "security", "python", "linux", "ci/cd", "microservices", "serverless", "cost optimization", "architecture"],
    "Business Analyst": ["sql", "excel", "power bi", "requirements gathering", "jira", "stakeholder management", "process mapping", "agile", "documentation", "tableau", "brd", "use cases", "visio", "gap analysis", "reporting"],
    "Full Stack Developer": ["react", "node.js", "python", "javascript", "sql", "mongodb", "docker", "git", "rest api", "typescript", "html", "css", "aws", "redis", "testing"],
}

STOP_WORDS = {
    "the", "and", "for", "are", "with", "this", "that", "from", "have", "has", "will", "can", "you", "our",
    "your", "their", "been", "more", "also", "into", "its", "was", "not", "all", "but", "any", "one", "each",
    "they", "we", "an", "in", "of", "to", "a", "is", "it", "be", "as", "at", "or", "on", "do", "if",
    "by", "up", "so", "no", "he", "she", "his", "her", "him", "my", "me", "us", "about", "who", "how",
    "what", "when", "where", "which", "within", "across", "over", "under",
}


def get_tokens(text):
    return re.findall(r"[a-zA-Z0-9][a-zA-Z0-9+#.\-/]*", text.lower())


def _join_non_empty(parts: Iterable[str]):
    return "\n".join(part for part in parts if part).strip()


def extract_pdf(file_bytes):
    errors = []

    try:
        reader = PdfReader(io.BytesIO(file_bytes))
        if getattr(reader, "is_encrypted", False):
            try:
                reader.decrypt("")
            except Exception:
                pass

        pages = [(page.extract_text() or "") for page in reader.pages]
        text = _join_non_empty(pages)
        if text:
            return text, ""
        errors.append("PyPDF2 extracted no selectable text")
    except Exception as exc:
        errors.append(f"PyPDF2 failed: {type(exc).__name__}")

    try:
        with pdfplumber.open(io.BytesIO(file_bytes)) as pdf:
            pages = [(page.extract_text() or "") for page in pdf.pages]
        text = _join_non_empty(pages)
        if text:
            return text, ""
        errors.append("pdfplumber extracted no selectable text")
    except Exception as exc:
        errors.append(f"pdfplumber failed: {type(exc).__name__}")

    details = "; ".join(errors)
    return "", (
        "Could not extract text from this PDF. "
        "If it is image-only, run OCR first. "
        f"Details: {details}."
    )


def extract_docx(file_bytes):
    try:
        document = docx.Document(io.BytesIO(file_bytes))
        text = "\n".join(paragraph.text for paragraph in document.paragraphs).strip()
        if not text:
            return "", "The DOCX file was opened, but no text was found."
        return text, ""
    except Exception:
        return "", "The uploaded DOCX file could not be read."


def extract_txt(file_bytes):
    for encoding in ("utf-8", "utf-8-sig", "cp1252", "latin-1"):
        try:
            text = file_bytes.decode(encoding).strip()
            if text:
                return text, ""
        except Exception:
            continue
    return "", "The uploaded TXT file could not be decoded. Save it as UTF-8 and try again."


def extract_resume_text(uploaded_file):
    file_extension = Path(uploaded_file.name).suffix.lower()
    file_bytes = uploaded_file.getvalue()

    if file_extension == ".pdf":
        return extract_pdf(file_bytes)
    if file_extension == ".docx":
        return extract_docx(file_bytes)
    if file_extension == ".txt":
        return extract_txt(file_bytes)
    return "", "Please upload a PDF, DOCX, or TXT file."


def extract_skills(text, job):
    tokens = set(get_tokens(text))
    words = get_tokens(text)
    bigrams = {words[index] + " " + words[index + 1] for index in range(len(words) - 1)}
    all_tokens = tokens | bigrams

    matched = []
    missing = []
    for skill in JOBS[job]:
        (matched if skill in all_tokens else missing).append(skill)
    return matched, missing


def extract_keywords(resume_text, jd_text):
    jd_tokens = Counter(word for word in get_tokens(jd_text) if len(word) > 3 and word not in STOP_WORDS)
    resume_tokens = set(word for word in get_tokens(resume_text) if len(word) > 3)
    resume_words = [word for word in get_tokens(resume_text) if word not in STOP_WORDS]
    resume_bigrams = {resume_words[index] + " " + resume_words[index + 1] for index in range(len(resume_words) - 1)}

    top_jd_terms = {word for word, _ in jd_tokens.most_common(40)}
    missing_keywords = sorted(word for word in top_jd_terms if word not in resume_tokens and word not in resume_bigrams)
    return missing_keywords[:20]


def compute_score(matched, missing):
    total = len(matched) + len(missing)
    return round(len(matched) / total * 100) if total else 0


for key, default in {"result": None, "last_error": ""}.items():
    if key not in st.session_state:
        st.session_state[key] = default


st.title("Resume Scorer")
st.caption("Upload a resume, paste a job description, and review the skill and keyword match.")

with st.form("resume_analysis_form"):
    job_type = st.selectbox("Job role", list(JOBS.keys()))
    uploaded = st.file_uploader("Resume file", type=["pdf", "docx", "txt"])
    jd_text = st.text_area("Job description", height=220, placeholder="Paste the full job description here.")
    submitted = st.form_submit_button("Analyze resume")

if submitted:
    st.session_state.result = None
    st.session_state.last_error = ""

    if uploaded is None:
        st.warning("Please upload a resume file.")
    elif not jd_text.strip():
        st.warning("Please paste a job description.")
    else:
        resume_text, error_message = extract_resume_text(uploaded)

        if error_message:
            st.session_state.last_error = error_message
            st.error(error_message)
        else:
            matched, missing_skills = extract_skills(resume_text, job_type)
            missing_keywords = extract_keywords(resume_text, jd_text)
            score = compute_score(matched, missing_skills)

            st.session_state.result = {
                "score": score,
                "job": job_type,
                "matched": matched,
                "missing_skills": missing_skills,
                "missing_keywords": missing_keywords,
            }

result = st.session_state.result
if result:
    score = result["score"]
    st.subheader("Match Score")
    st.metric(label=result["job"], value=f"{score}%")
    st.progress(score / 100)

    col_left, col_right = st.columns(2)
    with col_left:
        with st.expander("Missing skills", expanded=True):
            if result["missing_skills"]:
                for skill in result["missing_skills"]:
                    st.write(f"- {skill}")
            else:
                st.success("All key skills detected.")

    with col_right:
        with st.expander("Missing keywords", expanded=True):
            if result["missing_keywords"]:
                for keyword in result["missing_keywords"]:
                    st.write(f"- {keyword}")
            else:
                st.success("Good keyword coverage.")
