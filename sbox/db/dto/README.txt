Polymorphism:
1. An entity class should be final if no other classes derive from it.
2. Any field of non-final type should be annotated with:
   @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")

Bidirectional references:
1. If an entity class is connetted to another with bidirectional reference, both
   types should be anotated with:
   @JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope={{className}}.class, property="@id")
