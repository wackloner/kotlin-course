package ru.spbau.mit
import kotlin.test.assertEquals
import org.junit.Test
import ru.spbau.mit.tex.TexException
import ru.spbau.mit.tex.document

class DSLTest {
    @Test
    fun testSimple() {
        val actual = document {
            documentClass("beamer")
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test(expected = TexException::class)
    fun testNoDocumentClass() {
        document {}.toString()
    }

    @Test(expected = TexException::class)
    fun testManyDocumentClasses() {
        document {
            documentClass("first")
            documentClass("second")
        }.toString()
    }

    @Test
    fun testUsePackage() {
        val actual = document {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            usePackage("fontenc", "T2A")
            usePackage("inputenc", "utf8")
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian, english]{babel}
            |\usepackage[T2A]{fontenc}
            |\usepackage[utf8]{inputenc}
            |\begin{document}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testLists() {
        val actual = document {
            documentClass("beamer")
            usePackage("listings")
            itemize {
                item {
                    + "first"
                    itemize {
                        item{
                            + "first first"
                        }
                        item {
                            + "first second"
                        }
                    }
                }
                item {
                    + "second"
                }
            }
            enumerate("I") {
                item {
                    + "first"
                }
                item {
                    + "second"
                }
            }
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\usepackage{listings}
            |\begin{document}
            |  \begin{itemize}
            |    \item
            |      first
            |      \begin{itemize}
            |        \item
            |          first first
            |        \item
            |          first second
            |      \end{itemize}
            |    \item
            |      second
            |  \end{itemize}
            |  \begin{enumerate}[I]
            |    \item
            |      first
            |    \item
            |      second
            |  \end{enumerate}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testFrame() {
        val actual = document {
            documentClass("beamer")
            frame("first") {
                itemize {
                    item {
                        + "lol"
                    }
                }
            }
            frame("second", "size=big")
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |  \begin{frame}
            |    \frametitle{first}
            |    \begin{itemize}
            |      \item
            |        lol
            |    \end{itemize}
            |  \end{frame}
            |  \begin{frame}[size=big]
            |    \frametitle{second}
            |  \end{frame}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testMath() {
        val actual = document {
            documentClass("beamer")
            math {
                + "F_n = F_{n - 1} + F_{n - 2}"
                + "\\sum_{i = 0}^n 2^i = 2^{n + 1} - 1"
            }
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |  \begin{math}
            |    F_n = F_{n - 1} + F_{n - 2}
            |    \sum_{i = 0}^n 2^i = 2^{n + 1} - 1
            |  \end{math}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testAlignments() {
        val actual = document {
            documentClass("beamer")
            center {
                math {
                    + "Ax = \\lambda x"
                }
            }
            right {
                itemize {
                    item {
                        + "Hello, world!"
                    }
                }
            }
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |  \begin{center}
            |    \begin{math}
            |      Ax = \lambda x
            |    \end{math}
            |  \end{center}
            |  \begin{right}
            |    \begin{itemize}
            |      \item
            |        Hello, world!
            |    \end{itemize}
            |  \end{right}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testCustomTag() {
        val actual = document {
            documentClass("beamer")
            customTag("theorem") {
                + "Kotlin is great."
            }
            customTag("minipage", "position=3px")
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |  \begin{theorem}
            |    Kotlin is great.
            |  \end{theorem}
            |  \begin{minipage}[position=3px]
            |  \end{minipage}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }

    @Test
    fun testRepoExample() {
        val rows = listOf("first", "second")
        val actual = document {
            documentClass("beamer")
            usePackage("babel", "russian")
            frame("frametitle", "arg1=arg2") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
                    }
                }

                customTag("pyglist", "language=kotlin") {
                    + "val a = 5"
                    + "print(a)"
                }
            }
        }.toString().trim()

        val expected = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \begin{frame}[arg1=arg2]
            |    \frametitle{frametitle}
            |    \begin{itemize}
            |      \item
            |        first text
            |      \item
            |        second text
            |    \end{itemize}
            |    \begin{pyglist}[language=kotlin]
            |      val a = 5
            |      print(a)
            |    \end{pyglist}
            |  \end{frame}
            |\end{document}
        """.trimMargin()

        assertEquals(expected, actual)
    }
}
