# Scalak

Small extensions to [scalaz](http://github.com/scalaz/scalaz) and
examples for exploring and teaching the software engineering
benefits of defining algebraic data types as initial F-algebras.

# Getting scalak

We are working on the proper publishing to Bintray's jcenter and
possibly Maven Central.
Meanwhile, you can resolve the dependency on scalak as follows.

First, install the [Bintray sbt plugin](https://github.com/softprops/bintray-sbt).

Then add these lines to your `build.sbt`.

    resolvers += bintray.Opts.resolver.repo("laufer", "maven")

    libraryDependencies += "edu.luc.etl" %% "scalak" % "0.1.1"

You can also just clone this project and play around with the example
worksheets.

# Quick Start

Natural numbers as the initial algebra for the `Option` endofunctor.

    import scalaz._
    import Scalaz._
    import scalak._

    type Nat = µ[Option]

    val zero:         Nat = In(None)
    def succ(n: Nat): Nat = In(Some(n))

    val two   = succ(succ(zero))
    val three = succ(three)

Conversion to `Int` as a catamorphism.

    def toInt: Algebra[Option, Int] = {
      case None    => 0
      case Some(n) => n + 1
    }

    three cata toInt assert_=== 3

Conversion from `Int` as an anamorphism.

    def fromInt: Coalgebra[Option, Int] = (n: Int) => {
      require { n >= 0 }
      if   (n == 0) None
      else          Some(n - 1)
    }

    µ.unfold(7)(fromInt) cata toInt assert_=== 7

Addition as another catamorphism.

    def plus(m: Nat): Algebra[Option, Nat] = {
      case None    => m
      case Some(n) => succ(n)
    }

    two cata plus(three) cata toInt assert_=== 5

# Guide to Example Worksheets

We recommend looking at them in this order:

1. `expressions`
1. `natf`
1. `natoption`
1. `natHigherKinded`
1. `mylist`
1. `orgchart`

# Glossary

TODO

# References

- [Understanding F-Algebras](https://www.fpcomplete.com/user/bartosz/understanding-algebras)
- [F-Algebras (Wikipedia)](http://en.wikipedia.org/wiki/F-algebra)
- [Haskell recursion patterns library reference](http://hackage.haskell.org/package/pointless-haskell-0.0.8/docs/Generics-Pointless-RecursionPatterns.html)
