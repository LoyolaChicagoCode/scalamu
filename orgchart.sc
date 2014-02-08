import scalaz.Cofree
import scalaz.Functor
import scalaz.Monad
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.string._     // for assert_=== to work on strings
import scalaz.std.tuple._      // provides standard instances of tuple types
import scalaz.std.function._   // provides standard instances of common function types
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map
import scalaz.syntax.arrow._
import scalaz.syntax.arrow._   // provides arrow operators used below
import scalaz.syntax.id._      // provides |> (forward pipe like in F#)

import edu.luc.cs.scalaz._           // algebra types
import edu.luc.cs.scalaz.CofreeOps._ // injected cata method

/**
 * Endofunctor for (generic) F-algebra in the category Scala types.
 *
 * data NodeF[+A] = P | OU(A*)
 *
 * (This is largely equivalent to rose trees.)
 *
 * @tparam A carrier object of the F-algebra
 */
sealed trait NodeF[+A]
case object P extends NodeF[Nothing]
case class OU[A](children: A*) extends NodeF[A]

/**
 * Implicit value for declaring NodeF as a Functor in scalaz.
 */
implicit def NodeFunctor[T]: Functor[NodeF] = new Functor[NodeF] {
  def map[A, B](fa: NodeF[A])(f: A => B): NodeF[B] = fa match {
    case P => P
    case OU(cs @ _*) => OU(cs map f: _*)
  }
}

// TODO NodeMonad

/**
 * Fixed point of ExprF as carrier object for initial algebra.
 */
type Node[+T] = Cofree[NodeF, T]

/**
 * Factory methods for convenience.
 */
def p[T](value: T): Node[T]                      = Cofree(value, P)
def ou[T](value: T, children: Node[T]*): Node[T] = Cofree(value, OU(children: _*))
val org =
  ou(("The Outfit", 50),
    p(("CEO", 140)),
    p(("Assistant to CEO", 60)),
    ou(("Retail Dept", 70),
      p(("Dir of Retail", 120)),
      p(("Asst Dir of Retail", 90)),
      p(("Retail Clerk", 50))
    ),
    ou(("IT Dept", 130),
      p(("Dir of IT", 110)),
      p(("IT Specialist", 85))
    )
  )

org.map(_._1.length).head assert_=== 10

def size[A]: Algebra[A, NodeF, Int] = _ => {
  case P => 1
  case OU(cs @ _*) => cs.sum
}

org.cata(size) assert_=== 7

def depth[A]: Algebra[A, NodeF, Int] = _ => {
  case P => 1
  case OU(cs @ _*) => 1 + cs.max
}

org.cata(depth) assert_=== 3

def incBy(perc: Float)(num: Int): Int = scala.math.round(num.toFloat * (100 + perc) / 100)

val orgAfterRaise = org map (incBy(2.5f) _).second

orgAfterRaise.tail.asInstanceOf[OU[Node[(String, Int)]]].children(0).head._2 assert_=== 144
// TODO scalaz lenses

val orgSanitized = orgAfterRaise map { _._1 }

orgSanitized.head assert_=== "The Outfit"

println("yahoo")