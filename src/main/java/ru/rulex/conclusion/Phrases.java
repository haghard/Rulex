/*
 * Copyright 2013 Project Forward Conclusion Contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.rulex.conclusion;

/**
 * Factory enum for Phrases implementations which we support out-of-the-box.
 * 
 */
public enum Phrases
{

  ALL_TRUE
  {
    @Override
    public MutableAbstractPhrase<Object> getConclusionPhrase()
    {
      return MutableAbstractPhrase.all();
    }

    @Override
    public ImmutableAbstractPhrase<Object> getImmutableConclusionPhrase()
    {
      return ImmutableAbstractPhrase.all();
    }
  },

  ANY_TRUE
  {
    @Override
    public MutableAbstractPhrase<Object> getConclusionPhrase()
    {
      return MutableAbstractPhrase.any();
    }

    @Override
    public ImmutableAbstractPhrase<Object> getImmutableConclusionPhrase()
    {
      return ImmutableAbstractPhrase.any();
    }
  };

  public abstract <T> MutableAbstractPhrase<T> getConclusionPhrase();

  public abstract <T> ImmutableAbstractPhrase<T> getImmutableConclusionPhrase();

  /*
  public <T, E> AbstractPhrase<T, E> withNarrowedType()
  {
    return (AbstractPhrase<T>) this.<T> getConclusionPhrase();
  }
  */

}