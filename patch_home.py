import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    text = f.read()

old_header = """        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning, $userName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Read. Study. Pray. Give.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onNavigateTab(CemaTab.PROFILE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }"""

new_header = """        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning, $userName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Read. Study. Pray. Give.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateTab(CemaTab.ANNOUNCEMENTS) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Announcements",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onNavigateTab(CemaTab.PROFILE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }"""

text = text.replace(old_header, new_header)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(text)

print("Home header patched.")
