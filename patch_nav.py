import re

with open("app/src/main/java/com/example/ui/navigation/CemaNavigation.kt", "r") as f:
    text = f.read()

nav_item = '    CemaNavItem(CemaTab.ANNOUNCEMENTS, "Announcements", Icons.Filled.Campaign, Icons.Outlined.Campaign),\n'

if 'CemaTab.ANNOUNCEMENTS' not in text:
    text = text.replace(
        'CemaNavItem(CemaTab.SERMONS, "Sermons", Icons.Filled.Headset, Icons.Outlined.Headset),',
        'CemaNavItem(CemaTab.SERMONS, "Sermons", Icons.Filled.Headset, Icons.Outlined.Headset),\n' + nav_item
    )
    
    text = text.replace(
        'CemaTab.SERMONS -> SermonsScreen(viewModel = viewModel)',
        'CemaTab.SERMONS -> SermonsScreen(viewModel = viewModel)\n                CemaTab.ANNOUNCEMENTS -> AnnouncementsScreen(viewModel = viewModel)'
    )
    
with open("app/src/main/java/com/example/ui/navigation/CemaNavigation.kt", "w") as f:
    f.write(text)

print("Navigation patched.")
