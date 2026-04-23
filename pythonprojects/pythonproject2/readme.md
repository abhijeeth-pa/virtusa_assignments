# Resume Scorer

## Overview
This project is a Streamlit-based resume analysis app that compares a resume against a selected job role and a job description. It extracts text from uploaded files, matches skills, detects missing keywords, and generates a resume match score.

## Features
- Upload resume files in `PDF`, `DOCX`, or `TXT` format
- Select a target job role from predefined categories
- Paste a job description for comparison
- Extract resume text using multiple parsing approaches
- Show matched skills, missing skills, and missing keywords
- Display a score using Streamlit metrics and progress bars

## Tools Used
- Python 3
- Streamlit for web UI
- `PyPDF2` for PDF text extraction
- `pdfplumber` as a fallback PDF parser
- `python-docx` (`docx`) for DOCX reading
- `re` for tokenization and text matching
- `collections.Counter` for keyword frequency analysis
- `pathlib` and `io` for file handling

## File
- `resumescore.py` - Streamlit application entry point

## How to Run
Install dependencies if needed:

```bash
pip install streamlit PyPDF2 pdfplumber python-docx
```

Run the app:

```bash
streamlit run resumescore.py
```

## What the App Analyzes
- predefined job-specific skill lists
- resume text extracted from uploaded files
- frequent keywords from the job description
- missing skills and keyword gaps

## Screenshots

![Resume Scorer Results](./Screenshot%202026-04-15%20113030.png)



