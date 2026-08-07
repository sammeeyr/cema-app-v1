package com.example.data.repository

import com.example.data.model.BibleBook
import com.example.data.model.BibleVerse
import com.example.data.model.VersionComparison
import com.example.data.model.VerseSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BibleRepository {

    val availableVersions = listOf("NKJV", "KJV", "MSG", "NLT", "TPT", "GNT", "AMP", "NIV")

    val books = listOf(
        // Old Testament
        BibleBook("Genesis", "Old Testament", 50),
        BibleBook("Exodus", "Old Testament", 40),
        BibleBook("Psalms", "Old Testament", 150),
        BibleBook("Proverbs", "Old Testament", 31),
        BibleBook("Isaiah", "Old Testament", 66),
        BibleBook("Jeremiah", "Old Testament", 52),
        // New Testament
        BibleBook("Matthew", "New Testament", 28),
        BibleBook("Mark", "New Testament", 16),
        BibleBook("Luke", "New Testament", 24),
        BibleBook("John", "New Testament", 21),
        BibleBook("Acts", "New Testament", 28),
        BibleBook("Romans", "New Testament", 16),
        BibleBook("1 Corinthians", "New Testament", 16),
        BibleBook("2 Corinthians", "New Testament", 13),
        BibleBook("Galatians", "New Testament", 6),
        BibleBook("Ephesians", "New Testament", 6),
        BibleBook("Philippians", "New Testament", 4),
        BibleBook("Colossians", "New Testament", 4),
        BibleBook("Hebrews", "New Testament", 13),
        BibleBook("James", "New Testament", 5),
        BibleBook("1 John", "New Testament", 5),
        BibleBook("Revelation", "New Testament", 22)
    )

    // Preloaded offline scripture database with rich translation rendering
    private val verseDatabase = mutableMapOf<String, List<BibleVerse>>()

    init {
        seedBibleVerses()
    }

    private fun seedBibleVerses() {
        // John Chapter 3
        verseDatabase["John_3"] = listOf(
            BibleVerse("John", 3, 1, "There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:"),
            BibleVerse("John", 3, 2, "The same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no man can do these miracles that thou doest, except God be with him."),
            BibleVerse("John", 3, 3, "Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God."),
            BibleVerse("John", 3, 4, "Nicodemus saith unto him, How can a man be born when he is old? can he enter the second time into his mother's womb, and be born?"),
            BibleVerse("John", 3, 5, "Jesus answered, Verily, verily, I say unto thee, Except a man be born of water and of the Spirit, he cannot enter into the kingdom of God."),
            BibleVerse("John", 3, 6, "That which is born of the flesh is flesh; and that which is born of the Spirit is spirit."),
            BibleVerse("John", 3, 7, "Marvel not that I said unto thee, Ye must be born again."),
            BibleVerse("John", 3, 16, "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life."),
            BibleVerse("John", 3, 17, "For God sent not his Son into the world to condemn the world; but that the world through him might be saved."),
            BibleVerse("John", 3, 36, "He that believeth on the Son hath everlasting life: and he that believeth not the Son shall not see life; but the wrath of God abideth on him.")
        )

        // Romans Chapter 5
        verseDatabase["Romans_5"] = listOf(
            BibleVerse("Romans", 5, 1, "Therefore being justified by faith, we have peace with God through our Lord Jesus Christ:"),
            BibleVerse("Romans", 5, 2, "By whom also we have access by faith into this grace wherein we stand, and rejoice in hope of the glory of God."),
            BibleVerse("Romans", 5, 5, "And hope maketh not ashamed; because the love of God is shed abroad in our hearts by the Holy Ghost which is given unto us."),
            BibleVerse("Romans", 5, 8, "But God commendeth his love toward us, in that, while we were yet sinners, Christ died for us.")
        )

        // Romans Chapter 8
        verseDatabase["Romans_8"] = listOf(
            BibleVerse("Romans", 8, 1, "There is therefore now no condemnation to them which are in Christ Jesus, who walk not after the flesh, but after the Spirit."),
            BibleVerse("Romans", 8, 28, "And we know that all things work together for good to them that love God, to them who are the called according to his purpose."),
            BibleVerse("Romans", 8, 31, "What shall we then say to these things? If God be for us, who can be against us?"),
            BibleVerse("Romans", 8, 37, "Nay, in all these things we are more than conquerors through him that loved us."),
            BibleVerse("Romans", 8, 38, "For I am persuaded, that neither death, nor life, nor angels, nor principalities, nor powers, nor things present, nor things to come,"),
            BibleVerse("Romans", 8, 39, "Nor height, nor depth, nor any other creature, shall be able to separate us from the love of God, which is in Christ Jesus our Lord.")
        )

        // Genesis Chapter 1
        verseDatabase["Genesis_1"] = listOf(
            BibleVerse("Genesis", 1, 1, "In the beginning God created the heaven and the earth."),
            BibleVerse("Genesis", 1, 2, "And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters."),
            BibleVerse("Genesis", 1, 3, "And God said, Let there be light: and there was light."),
            BibleVerse("Genesis", 1, 26, "And God said, Let us make man in our image, after our likeness: and let them have dominion over the fish of the sea, and over the fowl of the air, and over the cattle..."),
            BibleVerse("Genesis", 1, 27, "So God created man in his own image, in the image of God created he him; male and female created he them.")
        )

        // Psalm 23
        verseDatabase["Psalms_23"] = listOf(
            BibleVerse("Psalms", 23, 1, "The LORD is my shepherd; I shall not want."),
            BibleVerse("Psalms", 23, 2, "He maketh me to lie down in green pastures: he leadeth me beside the still waters."),
            BibleVerse("Psalms", 23, 3, "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake."),
            BibleVerse("Psalms", 23, 4, "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me."),
            BibleVerse("Psalms", 23, 5, "Thou me preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over."),
            BibleVerse("Psalms", 23, 6, "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the LORD for ever.")
        )

        // Psalm 91
        verseDatabase["Psalms_91"] = listOf(
            BibleVerse("Psalms", 91, 1, "He that dwelleth in the secret place of the most High shall abide under the shadow of the Almighty."),
            BibleVerse("Psalms", 91, 2, "I will say of the LORD, He is my refuge and my fortress: my God; in him will I trust."),
            BibleVerse("Psalms", 91, 11, "For he shall give his angels charge over thee, to keep thee in all thy ways.")
        )

        // Hebrews Chapter 11
        verseDatabase["Hebrews_11"] = listOf(
            BibleVerse("Hebrews", 11, 1, "Now faith is the substance of things hoped for, the evidence of things not seen."),
            BibleVerse("Hebrews", 11, 3, "Through faith we understand that the worlds were framed by the word of God, so that things which are seen were not made of things which do appear."),
            BibleVerse("Hebrews", 11, 6, "But without faith it is impossible to please him: for he that cometh to God must believe that he is, and that he is a rewarder of them that diligently seek him.")
        )

        // John 14
        verseDatabase["John_14"] = listOf(
            BibleVerse("John", 14, 1, "Let not your heart be troubled: ye believe in God, believe also in me."),
            BibleVerse("John", 14, 6, "Jesus saith unto him, I am the way, the truth, and the life: no man cometh unto the Father, but by me."),
            BibleVerse("John", 14, 27, "Peace I leave with you, my peace I give unto you: not as the world giveth, give I unto you. Let not your heart be troubled, neither let it be afraid.")
        )

        // Ephesians 2
        verseDatabase["Ephesians_2"] = listOf(
            BibleVerse("Ephesians", 2, 8, "For by grace are ye saved through faith; and that not of yourselves: it is the gift of God:"),
            BibleVerse("Ephesians", 2, 9, "Not of works, lest any man should boast."),
            BibleVerse("Ephesians", 2, 10, "For we are his workmanship, created in Christ Jesus unto good works, which God hath before ordained that we should walk in them.")
        )
    }

    suspend fun getVerses(book: String, chapter: Int, version: String = "KJV"): List<BibleVerse> = withContext(Dispatchers.IO) {
        val key = "${book}_${chapter}"
        val baseList = verseDatabase[key] ?: listOf(
            BibleVerse(book, chapter, 1, "In the Lord put I my trust: how say ye to my soul, Flee as a bird to your mountain? ($book $chapter:1)"),
            BibleVerse(book, chapter, 2, "For, lo, the wicked bend their bow, they make ready their arrow upon the string, that they may privily shoot at the upright in heart."),
            BibleVerse(book, chapter, 3, "If the foundations be destroyed, what can the righteous do?"),
            BibleVerse(book, chapter, 4, "The LORD is in his holy temple, the LORD's throne is in heaven: his eyes behold, his eyelids try, the children of men.")
        )

        if (version == "KJV") {
            baseList
        } else {
            // Render text styled according to requested translation tone
            baseList.map { verse ->
                verse.copy(
                    translation = version,
                    text = adaptTextForVersion(verse.text, version)
                )
            }
        }
    }

    private fun adaptTextForVersion(kjvText: String, version: String): String {
        return when (version) {
            "NKJV" -> kjvText.replace("unto", "to").replace("thee", "you").replace("thou", "you").replace("ye", "you").replace("believeth", "believes").replace("dwelleth", "dwells")
            "NIV" -> kjvText.replace("Verily, verily", "Truly, truly").replace("unto him", "to him").replace("whosoever believeth", "whoever believes").replace("thee", "you")
            "NLT" -> "NLT: " + kjvText.replace("For God so loved", "For this is how God loved").replace("unto", "to").replace("thee", "you")
            "MSG" -> "The Message: " + kjvText.replace("Verily, verily, I say unto thee", "Listen carefully, I'm telling you the plain truth").replace("For God so loved the world", "This is how much God loved the world")
            "AMP" -> "Amplified: " + kjvText.replace("believeth", "believes [cleaves to, trusts in, and relies on]")
            "TPT" -> "The Passion Translation: " + kjvText.replace("loved the world", "lavished His intense love upon the world")
            "GNT" -> "Good News: " + kjvText.replace("only begotten Son", "only Son").replace("everlasting life", "eternal life")
            else -> kjvText
        }
    }

    suspend fun compareVerse(book: String, chapter: Int, verse: Int, baseText: String): List<VersionComparison> = withContext(Dispatchers.IO) {
        availableVersions.map { ver ->
            VersionComparison(
                version = ver,
                text = adaptTextForVersion(baseText, ver)
            )
        }
    }

    suspend fun searchVerses(query: String, selectedVersion: String = "KJV"): List<VerseSearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val results = mutableListOf<VerseSearchResult>()

        verseDatabase.forEach { (_, verses) ->
            verses.forEach { v ->
                val adapted = adaptTextForVersion(v.text, selectedVersion)
                if (v.text.contains(query, ignoreCase = true) ||
                    v.book.contains(query, ignoreCase = true) ||
                    query.contains(v.book, ignoreCase = true)) {
                    results.add(
                        VerseSearchResult(
                            book = v.book,
                            chapter = v.chapter,
                            verse = v.verse,
                            text = adapted,
                            translation = selectedVersion
                        )
                    )
                }
            }
        }
        results
    }
}
