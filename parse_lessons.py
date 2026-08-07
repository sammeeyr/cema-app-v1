import re

with open('DE.txt', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace form feeds
text = text.replace('\x0c', '')

lessons = []
# split by LEVEL
levels = re.split(r'LEVEL (\d+)', text)
for i in range(1, len(levels), 2):
    level_num = int(levels[i])
    level_content = levels[i+1]
    
    # split by LESSON
    lesson_splits = re.split(r'LESSON (\d+)', level_content)
    for j in range(1, len(lesson_splits), 2):
        lesson_num = int(lesson_splits[j])
        content = lesson_splits[j+1]
        
        if lesson_num == 1 and level_num == 2:
            print(content[:500])

