package me.chuwy.otusbats


trait Show[A] {
  def show(a: A): String
}

object Show {

  // 1.1 Instances (`Int`, `String`, `Boolean`)
  implicit val intShow: Show[Int] = fromJvm
  implicit val stringShow: Show[String] = fromJvm
  implicit val booleanShow: Show[Boolean] = fromJvm


  // 1.2 Instances with conditional implicit
  implicit def listShow[A](implicit ev: Show[A]): Show[List[A]] =
   fromFunction((a: List[A]) => mkString_(a, "[", "]", ","))

  implicit def setShow[A](implicit ev: Show[A]): Show[Set[A]] =
    fromFunction((a: Set[A]) => mkString_(a, "[", "]", ","))



  // 2. Summoner (apply)
  def apply[A](implicit ev: Show[A]): Show[A] = ev
  // 3. Syntax extensions

  implicit class ShowOps[A](a: A) {
    def show(implicit ev: Show[A]): String = ev.show(a)


    def mkString_[B](begin: String, end: String, separator: String)(implicit S: Show[B], ev: A <:< List[B]): String = {
      // with `<:<` evidence `isInstanceOf` is safe!
      val casted: List[B] = a.asInstanceOf[List[B]]
      Show.mkString_(casted, separator, begin, end)
    }

  }

  /** Transform list of `A` into `String` with custom separator, beginning and ending.
   *  For example: "[a, b, c]" from `List("a", "b", "c")`
   *
   *  @param separator. ',' in above example
   *  @param begin. '[' in above example
   *  @param end. ']' in above example
//   */
  def mkString_[A: Show](list: Iterable[A], begin: String, end: String, separator: String): String =
    list.map(a => a.show).mkString(begin,separator,end)

  // 4. Helper constructors

  /** Just use JVM `toString` implementation, available on every object */
  def fromJvm[A]: Show[A] = (a: A) => a.toString


  /** Provide a custom function to avoid `new Show { ... }` machinery */
  def fromFunction[A](f: A => String): Show[A] = (a: A) => f(a)

}
