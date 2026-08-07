import re

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "r") as f:
    text = f.read()

text = text.replace(
    "enum class CemaTab {\n    HOME, BIBLE, STUDY, NOTEBOOK, GIVE, AI_ASSISTANT, PROFILE, SETTINGS, SERMONS\n}",
    "enum class CemaTab {\n    HOME, BIBLE, STUDY, NOTEBOOK, GIVE, AI_ASSISTANT, PROFILE, SETTINGS, SERMONS, ANNOUNCEMENTS\n}"
)

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "w") as f:
    f.write(text)

print("Enum patched.")
