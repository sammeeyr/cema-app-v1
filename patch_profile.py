import re

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    text = f.read()

if "Sign In with Google" not in text:
    sign_out_button = """
        val isUserSignedIn by viewModel.isUserSignedIn.collectAsState()
        
        if (isUserSignedIn) {
            OutlinedButton(
                onClick = {
                    viewModel.isUserSignedIn.value = false
                    viewModel.userName.value = "Guest"
                    viewModel.userEmail.value = ""
                    Toast.makeText(context, "Signed out of Google account", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        } else {
            Button(
                onClick = {
                    viewModel.signInWithGoogle(context)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In with Google")
            }
        }
"""
    
    # replace the old logout button
    import re
    text = re.sub(r'OutlinedButton\(\s*onClick = \{\s*Toast\.makeText\(context, "Signed out of Google account"[^)]*\)\.show\(\)\s*\},.*?Text\("Logout"\)\s*\}', sign_out_button, text, flags=re.DOTALL)
    
with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(text)
print("ProfileScreen patched.")
