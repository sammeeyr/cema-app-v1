package com.example.util

data class ParsedBibleReference(
    val book: String,
    val chapter: Int,
    val verse: Int? = null,
    val endVerse: Int? = null
) {
    fun toDisplayString(): String {
        return if (verse != null) {
            if (endVerse != null && endVerse != verse) {
                "$book $chapter:$verse-$endVerse"
            } else {
                "$book $chapter:$verse"
            }
        } else {
            "$book $chapter"
        }
    }
}

data class ScriptureMatch(
    val matchText: String,
    val startIndex: Int,
    val endIndex: Int,
    val parsedRef: ParsedBibleReference
)

object BibleReferenceParser {

    private val bookAliasMap = mapOf(
        // Old Testament
        "gen" to "Genesis", "genesis" to "Genesis", "ge" to "Genesis", "gn" to "Genesis",
        "exod" to "Exodus", "exodus" to "Exodus", "ex" to "Exodus", "exo" to "Exodus",
        "lev" to "Leviticus", "leviticus" to "Leviticus", "le" to "Leviticus", "lv" to "Leviticus",
        "num" to "Numbers", "numbers" to "Numbers", "nu" to "Numbers", "nm" to "Numbers",
        "deut" to "Deuteronomy", "deuteronomy" to "Deuteronomy", "dt" to "Deuteronomy", "de" to "Deuteronomy",
        "josh" to "Joshua", "joshua" to "Joshua", "jos" to "Joshua",
        "judg" to "Judges", "judges" to "Judges", "jdg" to "Judges", "jdgs" to "Judges",
        "ruth" to "Ruth", "rth" to "Ruth", "ru" to "Ruth",
        "1 samuel" to "1 Samuel", "1 sam" to "1 Samuel", "1samuel" to "1 Samuel", "1sam" to "1 Samuel", "i samuel" to "1 Samuel", "i sam" to "1 Samuel", "1s" to "1 Samuel",
        "2 samuel" to "2 Samuel", "2 sam" to "2 Samuel", "2samuel" to "2 Samuel", "2sam" to "2 Samuel", "ii samuel" to "2 Samuel", "ii sam" to "2 Samuel", "2s" to "2 Samuel",
        "1 kings" to "1 Kings", "1 kgs" to "1 Kings", "1kings" to "1 Kings", "1kgs" to "1 Kings", "i kings" to "1 Kings", "i kgs" to "1 Kings", "1k" to "1 Kings",
        "2 kings" to "2 Kings", "2 kgs" to "2 Kings", "2kings" to "2 Kings", "2kgs" to "2 Kings", "ii kings" to "2 Kings", "ii kgs" to "2 Kings", "2k" to "2 Kings",
        "1 chronicles" to "1 Chronicles", "1 chron" to "1 Chronicles", "1chronicles" to "1 Chronicles", "1chron" to "1 Chronicles", "i chronicles" to "1 Chronicles",
        "2 chronicles" to "2 Chronicles", "2 chron" to "2 Chronicles", "2chronicles" to "2 Chronicles", "2chron" to "2 Chronicles", "ii chronicles" to "2 Chronicles",
        "ezra" to "Ezra", "ezr" to "Ezra",
        "neh" to "Nehemiah", "nehemiah" to "Nehemiah", "ne" to "Nehemiah",
        "esth" to "Esther", "esther" to "Esther", "est" to "Esther",
        "job" to "Job", "jb" to "Job",
        "psalm" to "Psalms", "psalms" to "Psalms", "psa" to "Psalms", "ps" to "Psalms", "pss" to "Psalms",
        "prov" to "Proverbs", "proverbs" to "Proverbs", "pr" to "Proverbs", "prv" to "Proverbs",
        "eccl" to "Ecclesiastes", "ecclesiastes" to "Ecclesiastes", "ecc" to "Ecclesiastes",
        "song of solomon" to "Song of Solomon", "song of songs" to "Song of Solomon", "song" to "Song of Solomon", "sos" to "Song of Solomon",
        "isa" to "Isaiah", "isaiah" to "Isaiah", "is" to "Isaiah",
        "jer" to "Jeremiah", "jeremiah" to "Jeremiah", "je" to "Jeremiah",
        "lam" to "Lamentations", "lamentations" to "Lamentations", "la" to "Lamentations",
        "ezek" to "Ezekiel", "ezekiel" to "Ezekiel", "eze" to "Ezekiel",
        "dan" to "Daniel", "daniel" to "Daniel", "dn" to "Daniel",
        "hos" to "Hosea", "hosea" to "Hosea", "ho" to "Hosea",
        "joel" to "Joel", "jl" to "Joel",
        "amos" to "Amos", "am" to "Amos",
        "obad" to "Obadiah", "obadiah" to "Obadiah", "ob" to "Obadiah",
        "jonah" to "Jonah", "jon" to "Jonah", "jnh" to "Jonah",
        "mic" to "Micah", "micah" to "Micah", "mc" to "Micah",
        "nah" to "Nahum", "nahum" to "Nahum", "na" to "Nahum",
        "hab" to "Habakkuk", "habakkuk" to "Habakkuk", "hb" to "Habakkuk",
        "zeph" to "Zephaniah", "zephaniah" to "Zephaniah", "zep" to "Zephaniah",
        "hag" to "Haggai", "haggai" to "Haggai", "hg" to "Haggai",
        "zech" to "Zechariah", "zechariah" to "Zechariah", "zc" to "Zechariah",
        "mal" to "Malachi", "malachi" to "Malachi", "ml" to "Malachi",

        // New Testament
        "matt" to "Matthew", "matthew" to "Matthew", "mt" to "Matthew",
        "mark" to "Mark", "mrk" to "Mark", "mk" to "Mark",
        "luke" to "Luke", "luk" to "Luke", "lk" to "Luke",
        "john" to "John", "joh" to "John", "jn" to "John",
        "acts" to "Acts", "act" to "Acts", "ac" to "Acts",
        "rom" to "Romans", "romans" to "Romans", "ro" to "Romans", "rm" to "Romans",
        "1 corinthians" to "1 Corinthians", "1 cor" to "1 Corinthians", "1corinthians" to "1 Corinthians", "1cor" to "1 Corinthians", "i corinthians" to "1 Corinthians", "i cor" to "1 Corinthians",
        "2 corinthians" to "2 Corinthians", "2 cor" to "2 Corinthians", "2corinthians" to "2 Corinthians", "2cor" to "2 Corinthians", "ii corinthians" to "2 Corinthians", "ii cor" to "2 Corinthians",
        "gal" to "Galatians", "galatians" to "Galatians", "ga" to "Galatians",
        "eph" to "Ephesians", "ephesians" to "Ephesians", "ep" to "Ephesians",
        "phil" to "Philippians", "philippians" to "Philippians", "php" to "Philippians", "ph" to "Philippians",
        "col" to "Colossians", "colossians" to "Colossians", "cl" to "Colossians",
        "1 thessalonians" to "1 Thessalonians", "1 thess" to "1 Thessalonians", "i thessalonians" to "1 Thessalonians", "i thess" to "1 Thessalonians",
        "2 thessalonians" to "2 Thessalonians", "2 thess" to "2 Thessalonians", "ii thessalonians" to "2 Thessalonians", "ii thess" to "2 Thessalonians",
        "1 timothy" to "1 Timothy", "1 tim" to "1 Timothy", "i timothy" to "1 Timothy", "i tim" to "1 Timothy",
        "2 timothy" to "2 Timothy", "2 tim" to "2 Timothy", "ii timothy" to "2 Timothy", "ii tim" to "2 Timothy",
        "titus" to "Titus", "tit" to "Titus", "ti" to "Titus",
        "philem" to "Philemon", "philemon" to "Philemon", "phm" to "Philemon",
        "heb" to "Hebrews", "hebrews" to "Hebrews", "he" to "Hebrews",
        "james" to "James", "jas" to "James", "jm" to "James",
        "1 peter" to "1 Peter", "1 pet" to "1 Peter", "i peter" to "1 Peter", "i pet" to "1 Peter",
        "2 peter" to "2 Peter", "2 pet" to "2 Peter", "ii peter" to "2 Peter", "ii pet" to "2 Peter",
        "1 john" to "1 John", "1 jn" to "1 John", "i john" to "1 John", "i jn" to "1 John",
        "2 john" to "2 John", "2 jn" to "2 John", "ii john" to "2 John", "ii jn" to "2 John",
        "3 john" to "3 John", "3 jn" to "3 John", "iii john" to "3 John", "iii jn" to "3 John",
        "jude" to "Jude", "jud" to "Jude",
        "rev" to "Revelation", "revelation" to "Revelation", "re" to "Revelation"
    )

    private val scriptureRegex = Regex(
        "(?i)\\b((?:[123]|I{1,3})\\s+)?([A-Za-z]+(?:\\s+[A-Za-z]+)?)\\.?\\s+(\\d+)(?::(\\d+)(?:-(\\d+))?)?\\b"
    )

    fun resolveBookName(rawBook: String): String? {
        val cleaned = rawBook.trim().lowercase().removeSuffix(".")
        return bookAliasMap[cleaned]
    }

    fun parseReference(rawText: String): ParsedBibleReference? {
        val trimmed = rawText.trim()
        val match = scriptureRegex.find(trimmed) ?: return null

        val prefix = match.groups[1]?.value ?: ""
        val bookPart = match.groups[2]?.value ?: ""
        val chapterStr = match.groups[3]?.value ?: return null
        val verseStr = match.groups[4]?.value
        val endVerseStr = match.groups[5]?.value

        val combinedBookStr = (prefix + bookPart).trim()
        val canonicalBook = resolveBookName(combinedBookStr) ?: return null

        val chapter = chapterStr.toIntOrNull() ?: return null
        val verse = verseStr?.toIntOrNull()
        val endVerse = endVerseStr?.toIntOrNull()

        return ParsedBibleReference(
            book = canonicalBook,
            chapter = chapter,
            verse = verse,
            endVerse = endVerse
        )
    }

    fun findScriptureMatches(text: String): List<ScriptureMatch> {
        val matches = mutableListOf<ScriptureMatch>()
        scriptureRegex.findAll(text).forEach { match ->
            val prefix = match.groups[1]?.value ?: ""
            val bookPart = match.groups[2]?.value ?: ""
            val chapterStr = match.groups[3]?.value ?: return@forEach
            val verseStr = match.groups[4]?.value
            val endVerseStr = match.groups[5]?.value

            val combinedBookStr = (prefix + bookPart).trim()
            val canonicalBook = resolveBookName(combinedBookStr) ?: return@forEach

            val chapter = chapterStr.toIntOrNull() ?: return@forEach
            val verse = verseStr?.toIntOrNull()
            val endVerse = endVerseStr?.toIntOrNull()

            val parsedRef = ParsedBibleReference(
                book = canonicalBook,
                chapter = chapter,
                verse = verse,
                endVerse = endVerse
            )

            matches.add(
                ScriptureMatch(
                    matchText = match.value,
                    startIndex = match.range.first,
                    endIndex = match.range.last + 1,
                    parsedRef = parsedRef
                )
            )
        }
        return matches
    }
}
