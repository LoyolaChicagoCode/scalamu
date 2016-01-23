import scalaz.{ Equal, Functor, Show }
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.syntax.equal._   // for assert_===

import scalamu._                // algebra types and injected cata method

/*
 * In this example, we represent natural numbers
 * essentially as lists without item values:
 *
 * 0 = zero
 * 3 = succ(succ(succ(zero)))
 *
 * We can then define operations such as addition on these.
 */

/**
 * Endofunctor for (non-generic) F-algebra in the category Scala types:
 * {{{
 * data NatF[+A] = Zero | Succ(n: A)
 * }}}
 * @tparam A argument (type parameter) of the endofunctor
 */
sealed trait NatF[+A]
case object Zero extends NatF[Nothing]
case class Succ[+A](n: A) extends NatF[A]

/**
 * Implicit value for declaring `NatF` as an instance of
 * typeclass `Functor` in scalaz.
 */
implicit val NatFFunctor = new Functor[NatF] {
  def map[A, B](fa: NatF[A])(f: A => B): NatF[B] = fa match {
    case Zero    => Zero: NatF[B]
    case Succ(n) => Succ(f(n))
  }
}

/** Declaration of `NatF` as an instance of `Equal`. */
trait NatFEqual[A] extends Equal[NatF[A]] {
  implicit def A: Equal[A]
  override def equalIsNatural: Boolean = A.equalIsNatural
  override def equal(a1: NatF[A], a2: NatF[A]) = (a1, a2) match {
    case (Zero, Zero)       => true
    case (Succ(l), Succ(r)) => A.equal(l, r)
    case _ => false
  }
}
implicit def natFEqual[A](implicit A0: Equal[A]): Equal[NatF[A]] = new NatFEqual[A] {
  implicit def A = A0
}

/** Declaration of `NatF` as an instance of `Show`. */
implicit def natFShow[A](implicit A: Show[A]): Show[NatF[A]] = new Show[NatF[A]] {
  override def show(e: NatF[A]): scalaz.Cord = e match {
    case Zero    => "Zero"
    case Succ(r) => "Succ(" +: A.show(r) :+ ")"
  }
}


/**
 * Least fixpoint of `NatF` (recursive type based on `NatF`)
 * as carrier object for initial algebra.
 */
type Nat = µ[NatF]

/**
 * Factory methods for convenience.
 */
val zero: Nat         = In(Zero)
def succ(n: Nat): Nat = In(Succ(n))

// some instances
val one   = succ(zero)
val two   = succ(one)
val three = succ(two)

/**
 * Conversion to `Int` as an `NatF`-algebra
 * for carrier object `Int` in the category Scala types.
 */
val toInt: Algebra[NatF, Int] = {
  case Zero    => 0
  case Succ(n) => n + 1
}

// now we can fold the toInt algebra into instances
zero  cata toInt assert_=== 0
three cata toInt assert_=== 3

/**
 * Conversion from `Int` as an `NatF`-coalgebra
 * for carrier object `Int` in category Scala types
 * (generator for corecursion).
 */
val fromInt: Coalgebra[NatF, Int] = (n: Int) => {
  require { n >= 0 }
  if   (n == 0) Zero
  else          Succ(n - 1)
}

/*
 * Unfold is an anamorphism for unfolding a Nat from a coalgebra
 * such as fromInt. This is an example of corecursion.
 *
 * We need to convert the item values back to Unit before applying toInt
 * because Cofree is generic in the item type and preserves it.
 * To avoid this, we would need a non-generic version of Cofree.
 */
µ.unfold(0)(fromInt) cata toInt assert_=== 0
µ.unfold(7)(fromInt) cata toInt assert_=== 7

/**
 * Addition to a number `m` as an `NatF`-algebra for carrier object
 * `Nat` in the category Scala types.
 *
 * @param m the number to which we are adding the argument of the algebra
 */
def plus(m: Nat): Algebra[NatF, Nat] = {
  case Zero    => m
  case Succ(n) => succ(n)
}

zero  cata plus(zero)  cata toInt assert_=== 0
zero  cata plus(three) cata toInt assert_=== 3
three cata plus(zero)  cata toInt assert_=== 3
two   cata plus(three) cata toInt assert_=== 5

// structural equality should now work
(Zero: NatF[Unit]) assert_=== (Zero: NatF[Unit])
(Succ(Zero): NatF[NatF[Unit]]) assert_=== Succ(Zero)
assert { (Succ(Zero): NatF[NatF[Unit]]) =/= Zero }
zero assert_=== zero
three assert_=== three
assert { three =/= zero }

// imports required for checking equality and functor laws
// using Scalaz's ScalaCheck bindings
import scalaz.syntax.functor._
import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalaCheckBinding._
import scalaz.scalacheck.ScalazProperties._
import org.scalacheck.Arbitrary

implicit def NatFArbitrary[A](implicit a: Arbitrary[A]): Arbitrary[NatF[A]] =
  a map { a => (Succ(a): NatF[A]) }

// the equality and functor laws should both hold
equal.laws[NatF[Int]].check
functor.laws[NatF].check

println("■")
