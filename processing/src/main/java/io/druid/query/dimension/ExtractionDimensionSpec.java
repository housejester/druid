/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.druid.query.dimension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.StringUtils;
import io.druid.query.extraction.DimExtractionFn;

import java.nio.ByteBuffer;

/**
 */
public class ExtractionDimensionSpec implements DimensionSpec
{
  private static final byte CACHE_TYPE_ID = 0x1;

  private final String dimension;
  private final DimExtractionFn dimExtractionFn;
  private final String outputName;

  @JsonCreator
  public ExtractionDimensionSpec(
      @JsonProperty("dimension") String dimension,
      @JsonProperty("outputName") String outputName,
      @JsonProperty("dimExtractionFn") DimExtractionFn dimExtractionFn
  )
  {
    this.dimension = dimension;
    this.dimExtractionFn = dimExtractionFn;

    // Do null check for backwards compatibility
    this.outputName = outputName == null ? dimension : outputName;
  }

  @Override
  @JsonProperty
  public String getDimension()
  {
    return dimension;
  }

  @Override
  @JsonProperty
  public String getOutputName()
  {
    return outputName;
  }

  @Override
  @JsonProperty
  public DimExtractionFn getDimExtractionFn()
  {
    return dimExtractionFn;
  }

  @Override
  public byte[] getCacheKey()
  {
    byte[] dimensionBytes = StringUtils.toUtf8(dimension);
    byte[] dimExtractionFnBytes = dimExtractionFn.getCacheKey();

    return ByteBuffer.allocate(1 + dimensionBytes.length + dimExtractionFnBytes.length)
                     .put(CACHE_TYPE_ID)
                     .put(dimensionBytes)
                     .put(dimExtractionFnBytes)
                     .array();
  }

  @Override
  public boolean preservesOrdering()
  {
    return dimExtractionFn.preservesOrdering();
  }

  @Override
  public String toString()
  {
    return "ExtractionDimensionSpec{" +
           "dimension='" + dimension + '\'' +
           ", dimExtractionFn=" + dimExtractionFn +
           ", outputName='" + outputName + '\'' +
           '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExtractionDimensionSpec that = (ExtractionDimensionSpec) o;

    if (dimExtractionFn != null ? !dimExtractionFn.equals(that.dimExtractionFn) : that.dimExtractionFn != null)
      return false;
    if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;
    if (outputName != null ? !outputName.equals(that.outputName) : that.outputName != null) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = dimension != null ? dimension.hashCode() : 0;
    result = 31 * result + (dimExtractionFn != null ? dimExtractionFn.hashCode() : 0);
    result = 31 * result + (outputName != null ? outputName.hashCode() : 0);
    return result;
  }
}
