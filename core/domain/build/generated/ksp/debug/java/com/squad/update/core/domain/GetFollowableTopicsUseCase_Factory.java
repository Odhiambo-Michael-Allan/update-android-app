package com.squad.update.core.domain;

import com.squad.update.core.data.repository.TopicsRepository;
import com.squad.update.core.data.repository.UserDataRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class GetFollowableTopicsUseCase_Factory implements Factory<GetFollowableTopicsUseCase> {
  private final Provider<TopicsRepository> topicsRepositoryProvider;

  private final Provider<UserDataRepository> userDataRepositoryProvider;

  public GetFollowableTopicsUseCase_Factory(Provider<TopicsRepository> topicsRepositoryProvider,
      Provider<UserDataRepository> userDataRepositoryProvider) {
    this.topicsRepositoryProvider = topicsRepositoryProvider;
    this.userDataRepositoryProvider = userDataRepositoryProvider;
  }

  @Override
  public GetFollowableTopicsUseCase get() {
    return newInstance(topicsRepositoryProvider.get(), userDataRepositoryProvider.get());
  }

  public static GetFollowableTopicsUseCase_Factory create(
      Provider<TopicsRepository> topicsRepositoryProvider,
      Provider<UserDataRepository> userDataRepositoryProvider) {
    return new GetFollowableTopicsUseCase_Factory(topicsRepositoryProvider, userDataRepositoryProvider);
  }

  public static GetFollowableTopicsUseCase newInstance(TopicsRepository topicsRepository,
      UserDataRepository userDataRepository) {
    return new GetFollowableTopicsUseCase(topicsRepository, userDataRepository);
  }
}
