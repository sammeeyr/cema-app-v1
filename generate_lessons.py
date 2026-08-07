import re

with open("DE.txt", "r", encoding="utf-8") as f:
    text = f.read()

# Split by pages
pages = text.split('\x0c')

lessons = []
current_level = 0
current_lesson = 0

class LessonData:
    def __init__(self, level, num, title, author):
        self.level = level
        self.num = num
        self.title = title
        self.author = author
        self.paragraphs = []
        self.questions = []
        self.readPassage = ""
        self.memoryVerse = ""
        self.memoryVerseText = ""
        self.reflection = "Take time to reflect on this lesson and how it applies to your life."
        self.prayer = "Lord, thank You for Your Word. Help me to understand and apply these truths to my life. Amen."

current_data = None

for page in pages:
    lines = [line.strip() for line in page.split('\n') if line.strip()]
    if not lines: continue
    
    # Check if page indicates a level
    if "LEVEL " in lines[0] or (len(lines) > 1 and "LEVEL " in lines[1]):
        m = re.search(r'LEVEL (\d)', lines[0] + " " + (lines[1] if len(lines) > 1 else ""))
        if m:
            level_num = int(m.group(1))
            if level_num > current_level:
                current_level = level_num
    
    # Check if page is a new lesson start
    if any(line.startswith("LESSON ") for line in lines[:5]):
        # Find the LESSON line
        lesson_idx = next(i for i, line in enumerate(lines) if line.startswith("LESSON "))
        m = re.search(r'LESSON (\d+)', lines[lesson_idx])
        if m:
            num = int(m.group(1))
            # If it's a new lesson page (starts with title and By Author)
            if lesson_idx + 2 < len(lines) and lines[lesson_idx+2].startswith("By "):
                title = lines[lesson_idx+1]
                author = lines[lesson_idx+2][3:]
                
                current_data = LessonData(current_level, num, title, author)
                lessons.append(current_data)
                
                # Extract paragraphs
                text_lines = lines[lesson_idx+3:]
                current_para = []
                for line in text_lines:
                    current_para.append(line)
                    if line.endswith('.') or line.endswith('?') or line.endswith('!'):
                        if len(" ".join(current_para)) > 100:
                            current_data.paragraphs.append(" ".join(current_para))
                            current_para = []
                if current_para and len(" ".join(current_para)) > 50:
                    current_data.paragraphs.append(" ".join(current_para))
                    
    # Check if it's a questions page
    elif current_data and "DISCIPLESHIP QUESTIONS" in page:
        # Extract questions
        q_lines = [line for line in lines if re.match(r'^\d+\.', line)]
        for q in q_lines:
            current_data.questions.append(q)

for l in lessons:
    print(f"Level {l.level} Lesson {l.num}: {l.title} by {l.author}")
    print(f"  {len(l.paragraphs)} paragraphs, {len(l.questions)} questions")
