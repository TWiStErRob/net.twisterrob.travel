### -- android/feature/range/proguard.pro -- ###

# TODEL https://issuetracker.google.com/issues/312677256
# WARNING: app\build\intermediates\merged_java_res\release\mergeReleaseJavaResource\base.jar:
# R8: Unexpected reference to missing service class: META-INF/services/io.grpc.LoadBalancerProvider.
-dontwarn io.grpc.LoadBalancerProvider
# R8: Unexpected reference to missing service implementation class in META-INF/services/io.grpc.LoadBalancerProvider: io.grpc.internal.PickFirstLoadBalancerProvider.
-dontwarn io.grpc.internal.PickFirstLoadBalancerProvider
# R8: Unexpected reference to missing service class: META-INF/services/io.grpc.NameResolverProvider.
-dontwarn io.grpc.NameResolverProvider
# R8: Unexpected reference to missing service implementation class in META-INF/services/io.grpc.NameResolverProvider: io.grpc.internal.DnsNameResolverProvider.
-dontwarn io.grpc.internal.DnsNameResolverProvider
