# kotlinx.serialization: keep generated serializers for content models.
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class io.github.sreepv43.fablife.content.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class io.github.sreepv43.fablife.content.**$$serializer { *; }
