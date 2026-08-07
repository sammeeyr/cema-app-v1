import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    text = f.read()

text = text.replace(
    '<application',
    '<application\n        android:name=".CemaApplication"'
)

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(text)

print("Manifest patched.")
