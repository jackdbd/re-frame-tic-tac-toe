(ns re-frame-tic-tac-toe.config)

(def debug?
  "goog.DEBUG is a compile time constant provided by the Google Closure Compiler.
  It will be true when the build within project.clj is :optimization :none and
  false otherwise."
  ^boolean goog.DEBUG)
