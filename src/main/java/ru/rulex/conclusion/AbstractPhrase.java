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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class hierarchy is a <b> implementation </b> path of <b> pattern bridge
 * </b> http://en.wikipedia.org/wiki/Bridge_pattern
 * <p>
 * The <b> abstraction </b> path is a
 * {@code AbstractForwardConclusionPhrasesBuilder} class hierarchy
 * </p>
 * </p>
 */
public interface AbstractPhrase<T, E extends AssertionUnit<T>>
{

  abstract void addUnit( E ruleEntry );

  abstract Boolean evaluate();

  abstract void setEventClass( Class<T> clazz );

  abstract void setEvent( T event );

}