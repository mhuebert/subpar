(ns subpar.test.tests
  (:use [subpar.paredit :only [parse
                               get-opening-delimiter-index
                               get-closing-delimiter-index
                               in-comment?
                               in-code?
                               in-string?
                               double-quote-action
                               get-start-of-next-list
                               backward-up-fn
                               forward-fn
                               forward-slurp-vals
                               forward-barf-vals
                               backward-slurp-vals
                               backward-barf-vals
                               forward-delete-action
                               backward-delete-action
                               count-lines
                               close-expression-vals]]))

(defn run []
  (assert (= 4 (count-lines "\n\n\n\n" 0 2)))
  (assert (= 3 (count-lines "0\n\n\n\n" 0 2)))
  (assert (= 2 (count-lines "01\n\n\n\n" 0 2)))
  (assert (= 1 (count-lines "012\n\n\n\n" 0 2)))

  (assert (= -1 (get-opening-delimiter-index " ()"      0)))
  (assert (= -1 (get-opening-delimiter-index " ()"      1)))
  (assert (= 1  (get-opening-delimiter-index " ()"      2)))
  (assert (= -1 (get-opening-delimiter-index " ()"      3)))
  (assert (= -1 (get-opening-delimiter-index " () []"   3)))
  (assert (= -1 (get-opening-delimiter-index " () []"   4)))
  (assert (= 4  (get-opening-delimiter-index " () []"   5)))
  (assert (= -1 (get-opening-delimiter-index " () []"   6)))
  (assert (= 1  (get-opening-delimiter-index " ([a] )"  2)))
  (assert (= 2  (get-opening-delimiter-index " ([a] )"  3)))
  (assert (= 5  (get-opening-delimiter-index "([a]){b}" 6)))
  (assert (= 1  (get-opening-delimiter-index " (;a\nb)" 5)))
  
  (assert (= 3  (get-closing-delimiter-index " ()"          0)))
  (assert (= 3  (get-closing-delimiter-index " ()"          1)))
  (assert (= 2  (get-closing-delimiter-index " ()"          2)))
  (assert (= 3  (get-closing-delimiter-index " ()"          3)))
  (assert (= 6  (get-closing-delimiter-index " () []"       3)))
  (assert (= 6  (get-closing-delimiter-index " () []"       4)))
  (assert (= 5  (get-closing-delimiter-index " () []"       5)))
  (assert (= 6  (get-closing-delimiter-index " () []"       6)))
  (assert (= 6  (get-closing-delimiter-index " ([a] )"      2)))
  (assert (= 4  (get-closing-delimiter-index " ([a] )"      3)))
  (assert (= 7  (get-closing-delimiter-index "([a]){b}"     6)))
  (assert (= 10 (get-closing-delimiter-index " (;a\nb () )" 5)))
  
  (assert (= false (in-comment? (parse "a;b") 0)))
  (assert (= false (in-comment? (parse "a;b") 1)))
  (assert (= true  (in-comment? (parse "a;b") 2)))
  (assert (= true  (in-comment? (parse "a;b\nc") 3)))
  (assert (= false (in-comment? (parse "a;b\nc") 4)))
  (assert (= true  (in-comment? (parse "a;\"b\"") 3)))
  
  (assert (= true  (in-code? (parse "a;b") 0)))
  (assert (= true  (in-code? (parse "a;b") 1)))
  (assert (= false (in-code? (parse "a;b") 2)))
  (assert (= true  (in-code? (parse "a;b\nc") 4)))
  (assert (= false (in-code? (parse "a;\"b\"") 3)))
  
  (assert (= false (in-string? (parse "a;\"b\"") 0)))
  (assert (= false (in-string? (parse "a;\"b\"") 3)))
  (assert (= false (in-string? (parse "a \"b\"") 2)))
  (assert (= true  (in-string? (parse "a \"b\"") 3)))
  (assert (= true  (in-string? (parse "a \"b\"") 4)))

  (assert (= 0 (double-quote-action "" 0)))
  (assert (= 0 (double-quote-action "  " 1)))
  (assert (= 0 (double-quote-action "\"\"" 0)))
  (assert (= 2 (double-quote-action "\"\"" 1)))
  (assert (= 1 (double-quote-action "\" \"" 1)))
  (assert (= 1 (double-quote-action "\" \\\" \"" 2)))
  (assert (= 2 (double-quote-action "\" \\\" \"" 3)))
  (assert (= 0 (double-quote-action "; \" \"" 0)))
  (assert (= 3 (double-quote-action "; \" \"" 1)))
  (assert (= 3 (double-quote-action "; \" \"" 2)))
  (assert (= 3 (double-quote-action "; \" \"" 3)))
  (assert (= 3 (double-quote-action "; \" \"" 4)))

  (assert (= false (get-start-of-next-list ""           0)))
  (assert (= false (get-start-of-next-list " "          0)))
  (assert (= false (get-start-of-next-list "()  "       2)))
  (assert (= false (get-start-of-next-list "()"         1)))
  (assert (= 0     (get-start-of-next-list "() "        0)))
  (assert (= false (get-start-of-next-list ";()"        0)))
  (assert (= false (get-start-of-next-list ";[]"        0)))
  (assert (= false (get-start-of-next-list ";{}"        0)))
  (assert (= false (get-start-of-next-list ";\"\""      0)))
  (assert (= 1     (get-start-of-next-list " () "       0)))
  (assert (= 1     (get-start-of-next-list " [] "       0)))
  (assert (= 1     (get-start-of-next-list " {} "       0)))
  (assert (= 1     (get-start-of-next-list " \"\" "     0)))
  (assert (= false (get-start-of-next-list ";\"\""      0)))
  (assert (= false (get-start-of-next-list ";\"\""      0)))
  (assert (= false (get-start-of-next-list "();a\n()"   1)))
  (assert (= 5     (get-start-of-next-list "();a\n()"   2)))
  (assert (= 2     (get-start-of-next-list "( [] [])"   1)))
  (assert (= 5     (get-start-of-next-list "(aaa []())" 1)))

  (assert (= 0  (backward-up-fn ""              0)))
  (assert (= 0  (backward-up-fn " "             0)))
  (assert (= 1  (backward-up-fn " "             1)))
  (assert (= 1  (backward-up-fn " ( )"          2)))
  (assert (= 3  (backward-up-fn " ()"           3)))
  (assert (= 5  (backward-up-fn " ()\n;\n"      5)))
  (assert (= 3  (backward-up-fn " ( [ ])"       4)))
  (assert (= 3  (backward-up-fn " ( [ asdf])"   7)))
  (assert (= 3  (backward-up-fn " ( [ asdf])"   9)))
  (assert (= 1  (backward-up-fn " ( [ asdf])"   10)))
  (assert (= 11 (backward-up-fn " ( [ asdf])"   11)))
  (assert (= 13 (backward-up-fn " ( [ asdf])  " 13)))

  (assert (= 0  (forward-fn ""               0)))
  (assert (= 1  (forward-fn " "              0)))
  (assert (= 3  (forward-fn " ()"            0)))
  (assert (= 3  (forward-fn "\n()"           0)))
  (assert (= 11 (forward-fn " (asdf (a))"    0)))
  (assert (= 11 (forward-fn " (asdf (a))"    1)))
  (assert (= 6  (forward-fn " (asdf (a))"    2)))
  (assert (= 6  (forward-fn " (asdf (a))"    3)))
  (assert (= 10 (forward-fn " (asdf (a))"    6)))
  (assert (= 6  (forward-fn "((ab ) )"       1)))
  (assert (= 4  (forward-fn "((ab ) )"       2)))
  (assert (= 6  (forward-fn "((ab ) )"       4)))
  (assert (= 13 (forward-fn ";a\n[asdf {a}]" 0)))
  (assert (= 5  (forward-fn " asdf "         0)))
  (assert (= 5  (forward-fn " asdf "         2)))
  (assert (= 9  (forward-fn "( a ;b\n c)"    3)))
  (assert (= 4  (forward-fn "\"\\n\""    0)))

  (assert (= [\) 1 4 1]  (forward-slurp-vals "() a"          1)))
  (assert (= [\) 1 6 1]  (forward-slurp-vals "() (a)"        1)))
  (assert (= [\) 1 8 1]  (forward-slurp-vals "() (a b)"      1)))
  (assert (= [\) 1 10 2] (forward-slurp-vals "();c\n(a b)"   1)))
  (assert (= []          (forward-slurp-vals "() "           2)))
  (assert (= []          (forward-slurp-vals " () "          0)))
  (assert (= [\) 1 8 1]  (forward-slurp-vals "() \"a b\""    1)))
  (assert (= []          (forward-slurp-vals "({a \"b\"} c)" 6)))
  (assert (= [\) 4 7 1]  (forward-slurp-vals "(abc) a"       2)))

  (assert (= [\( 3 1 1] (backward-slurp-vals " a () "          4)))
  (assert (= [\( 2 0 1] (backward-slurp-vals "a () "           3)))
  (assert (= []         (backward-slurp-vals "a () "           2)))
  (assert (= [\( 6 1 1] (backward-slurp-vals " [ab] (c d) "    9)))
  (assert (= [\( 6 1 1] (backward-slurp-vals " {ab} (c d) "    8)))
  (assert (= [\( 7 1 1] (backward-slurp-vals " (a b) (c d) "   8)))
  (assert (= [\( 7 1 1] (backward-slurp-vals " \"a b\" (c d) " 8)))
  (assert (= []         (backward-slurp-vals "(a [{}])"        5)))

  (assert (= 0 (forward-delete-action ""        0)))
  (assert (= 0 (forward-delete-action "a"       1)))
  (assert (= 1 (forward-delete-action "a"       0)))
  (assert (= 3 (forward-delete-action "[]"      0)))
  (assert (= 2 (forward-delete-action "[]"      1)))
  (assert (= 0 (forward-delete-action "[a]"     2)))
  (assert (= 0 (forward-delete-action "[ ]"     2)))
  (assert (= 4 (forward-delete-action "( )"     0)))
  (assert (= 4 (forward-delete-action "(a)"     0))) 
  (assert (= 4 (forward-delete-action "\"a\""   0))) 
  (assert (= 3 (forward-delete-action "\"\""    0))) 
  (assert (= 0 (forward-delete-action "\" \""   2))) 
  (assert (= 3 (forward-delete-action "\\a"     0)))
  (assert (= 2 (forward-delete-action "\\a"     1)))
  (assert (= 3 (forward-delete-action "\"\\a\"" 1)))
  (assert (= 2 (forward-delete-action "\"\\a\"" 2)))

  (assert (= 0 (backward-delete-action ""       0)))
  (assert (= 0 (backward-delete-action " "      0)))
  (assert (= 1 (backward-delete-action " "      1)))
  (assert (= 0 (backward-delete-action "( )"    1)))
  (assert (= 4 (backward-delete-action "( )"    3)))
  (assert (= 3 (backward-delete-action "()"     2)))
  (assert (= 4 (backward-delete-action "(asdf)" 6)))
  (assert (= 2 (backward-delete-action "\\a"    1)))
  (assert (= 3 (backward-delete-action "\\a"    2)))
  (assert (= 2 (backward-delete-action "\"\""   1)))
  (assert (= 3 (backward-delete-action "\"\""   2)))
  (assert (= 2 (backward-delete-action "\"\\\"" 2)))
  (assert (= 3 (backward-delete-action "\"\\\"" 3)))

  (assert (= []               (backward-barf-vals "" 0)))
  (assert (= []               (backward-barf-vals "()" 1)))
  (assert (= [\( 0 2 true 1]  (backward-barf-vals "(a)" 1)))
  (assert (= [\( 0 3 false 1] (backward-barf-vals "(a b)" 1)))
  (assert (= [\( 0 3 false 2] (backward-barf-vals "(a\nb)" 1)))
  (assert (= []               (backward-barf-vals "(a b)" 5)))
  (assert (= []               (backward-barf-vals "(a b) " 5)))
  (assert (= [\[ 3 5 true 1]  (backward-barf-vals "(a [b]) " 4)))
  
  (assert (= []                 (forward-barf-vals "" 0)))
  (assert (= []                 (forward-barf-vals "()" 1)))
  (assert (= [\) 2 1 true 1 0]  (forward-barf-vals "(a)" 1)))
  (assert (= [\) 4 2 false 1 0] (forward-barf-vals "(a b)" 1)))
  (assert (= [\) 4 2 false 2 0] (forward-barf-vals "(a\nb)" 1)))
  (assert (= []                 (forward-barf-vals "(a b)" 5)))
  (assert (= []                 (forward-barf-vals "(a b) " 5)))
  (assert (= [\] 5 4 true 1 3]  (forward-barf-vals "(a [b]) " 4)))

  (assert (= [true 1 4 2] (close-expression-vals (parse "[   ]") 1))))
