package com.davidruffner.awsfiletransfer.messaging.entities;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: FileTransferRequest.proto

// Protobuf Java Version: 3.25.2
public final class FileTransferRequestOuterClass {
  private FileTransferRequestOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface FileTransferRequestOrBuilder extends
      // @@protoc_insertion_point(interface_extends:FileTransferRequest)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string fileName = 1;</code>
     * @return Whether the fileName field is set.
     */
    boolean hasFileName();
    /**
     * <code>required string fileName = 1;</code>
     * @return The fileName.
     */
    java.lang.String getFileName();
    /**
     * <code>required string fileName = 1;</code>
     * @return The bytes for fileName.
     */
    com.google.protobuf.ByteString
        getFileNameBytes();

    /**
     * <code>required bytes data = 2;</code>
     * @return Whether the data field is set.
     */
    boolean hasData();
    /**
     * <code>required bytes data = 2;</code>
     * @return The data.
     */
    com.google.protobuf.ByteString getData();

    /**
     * <code>optional string bucketName = 3;</code>
     * @return Whether the bucketName field is set.
     */
    boolean hasBucketName();
    /**
     * <code>optional string bucketName = 3;</code>
     * @return The bucketName.
     */
    java.lang.String getBucketName();
    /**
     * <code>optional string bucketName = 3;</code>
     * @return The bytes for bucketName.
     */
    com.google.protobuf.ByteString
        getBucketNameBytes();
  }
  /**
   * Protobuf type {@code FileTransferRequest}
   */
  public static final class FileTransferRequest extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:FileTransferRequest)
      FileTransferRequestOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use FileTransferRequest.newBuilder() to construct.
    private FileTransferRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private FileTransferRequest() {
      fileName_ = "";
      data_ = com.google.protobuf.ByteString.EMPTY;
      bucketName_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new FileTransferRequest();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return FileTransferRequestOuterClass.internal_static_FileTransferRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return FileTransferRequestOuterClass.internal_static_FileTransferRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              FileTransferRequestOuterClass.FileTransferRequest.class, FileTransferRequestOuterClass.FileTransferRequest.Builder.class);
    }

    private int bitField0_;
    public static final int FILENAME_FIELD_NUMBER = 1;
    @SuppressWarnings("serial")
    private volatile java.lang.Object fileName_ = "";
    /**
     * <code>required string fileName = 1;</code>
     * @return Whether the fileName field is set.
     */
    @java.lang.Override
    public boolean hasFileName() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required string fileName = 1;</code>
     * @return The fileName.
     */
    @java.lang.Override
    public java.lang.String getFileName() {
      java.lang.Object ref = fileName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          fileName_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string fileName = 1;</code>
     * @return The bytes for fileName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getFileNameBytes() {
      java.lang.Object ref = fileName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        fileName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DATA_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>required bytes data = 2;</code>
     * @return Whether the data field is set.
     */
    @java.lang.Override
    public boolean hasData() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required bytes data = 2;</code>
     * @return The data.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    public static final int BUCKETNAME_FIELD_NUMBER = 3;
    @SuppressWarnings("serial")
    private volatile java.lang.Object bucketName_ = "";
    /**
     * <code>optional string bucketName = 3;</code>
     * @return Whether the bucketName field is set.
     */
    @java.lang.Override
    public boolean hasBucketName() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string bucketName = 3;</code>
     * @return The bucketName.
     */
    @java.lang.Override
    public java.lang.String getBucketName() {
      java.lang.Object ref = bucketName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          bucketName_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string bucketName = 3;</code>
     * @return The bytes for bucketName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBucketNameBytes() {
      java.lang.Object ref = bucketName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        bucketName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasFileName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasData()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, fileName_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeBytes(2, data_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, bucketName_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, fileName_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, data_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, bucketName_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof FileTransferRequestOuterClass.FileTransferRequest)) {
        return super.equals(obj);
      }
      FileTransferRequestOuterClass.FileTransferRequest other = (FileTransferRequestOuterClass.FileTransferRequest) obj;

      if (hasFileName() != other.hasFileName()) return false;
      if (hasFileName()) {
        if (!getFileName()
            .equals(other.getFileName())) return false;
      }
      if (hasData() != other.hasData()) return false;
      if (hasData()) {
        if (!getData()
            .equals(other.getData())) return false;
      }
      if (hasBucketName() != other.hasBucketName()) return false;
      if (hasBucketName()) {
        if (!getBucketName()
            .equals(other.getBucketName())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasFileName()) {
        hash = (37 * hash) + FILENAME_FIELD_NUMBER;
        hash = (53 * hash) + getFileName().hashCode();
      }
      if (hasData()) {
        hash = (37 * hash) + DATA_FIELD_NUMBER;
        hash = (53 * hash) + getData().hashCode();
      }
      if (hasBucketName()) {
        hash = (37 * hash) + BUCKETNAME_FIELD_NUMBER;
        hash = (53 * hash) + getBucketName().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static FileTransferRequestOuterClass.FileTransferRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static FileTransferRequestOuterClass.FileTransferRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static FileTransferRequestOuterClass.FileTransferRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(FileTransferRequestOuterClass.FileTransferRequest prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code FileTransferRequest}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:FileTransferRequest)
        FileTransferRequestOuterClass.FileTransferRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return FileTransferRequestOuterClass.internal_static_FileTransferRequest_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return FileTransferRequestOuterClass.internal_static_FileTransferRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                FileTransferRequestOuterClass.FileTransferRequest.class, FileTransferRequestOuterClass.FileTransferRequest.Builder.class);
      }

      // Construct using FileTransferRequestOuterClass.FileTransferRequest.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        fileName_ = "";
        data_ = com.google.protobuf.ByteString.EMPTY;
        bucketName_ = "";
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return FileTransferRequestOuterClass.internal_static_FileTransferRequest_descriptor;
      }

      @java.lang.Override
      public FileTransferRequestOuterClass.FileTransferRequest getDefaultInstanceForType() {
        return FileTransferRequestOuterClass.FileTransferRequest.getDefaultInstance();
      }

      @java.lang.Override
      public FileTransferRequestOuterClass.FileTransferRequest build() {
        FileTransferRequestOuterClass.FileTransferRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public FileTransferRequestOuterClass.FileTransferRequest buildPartial() {
        FileTransferRequestOuterClass.FileTransferRequest result = new FileTransferRequestOuterClass.FileTransferRequest(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(FileTransferRequestOuterClass.FileTransferRequest result) {
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.fileName_ = fileName_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.data_ = data_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.bucketName_ = bucketName_;
          to_bitField0_ |= 0x00000004;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof FileTransferRequestOuterClass.FileTransferRequest) {
          return mergeFrom((FileTransferRequestOuterClass.FileTransferRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(FileTransferRequestOuterClass.FileTransferRequest other) {
        if (other == FileTransferRequestOuterClass.FileTransferRequest.getDefaultInstance()) return this;
        if (other.hasFileName()) {
          fileName_ = other.fileName_;
          bitField0_ |= 0x00000001;
          onChanged();
        }
        if (other.hasData()) {
          setData(other.getData());
        }
        if (other.hasBucketName()) {
          bucketName_ = other.bucketName_;
          bitField0_ |= 0x00000004;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        if (!hasFileName()) {
          return false;
        }
        if (!hasData()) {
          return false;
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {
                fileName_ = input.readBytes();
                bitField0_ |= 0x00000001;
                break;
              } // case 10
              case 18: {
                data_ = input.readBytes();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              case 26: {
                bucketName_ = input.readBytes();
                bitField0_ |= 0x00000004;
                break;
              } // case 26
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private java.lang.Object fileName_ = "";
      /**
       * <code>required string fileName = 1;</code>
       * @return Whether the fileName field is set.
       */
      public boolean hasFileName() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>required string fileName = 1;</code>
       * @return The fileName.
       */
      public java.lang.String getFileName() {
        java.lang.Object ref = fileName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            fileName_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string fileName = 1;</code>
       * @return The bytes for fileName.
       */
      public com.google.protobuf.ByteString
          getFileNameBytes() {
        java.lang.Object ref = fileName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          fileName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string fileName = 1;</code>
       * @param value The fileName to set.
       * @return This builder for chaining.
       */
      public Builder setFileName(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        fileName_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>required string fileName = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearFileName() {
        fileName_ = getDefaultInstance().getFileName();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>required string fileName = 1;</code>
       * @param value The bytes for fileName to set.
       * @return This builder for chaining.
       */
      public Builder setFileNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        fileName_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes data = 2;</code>
       * @return Whether the data field is set.
       */
      @java.lang.Override
      public boolean hasData() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>required bytes data = 2;</code>
       * @return The data.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>required bytes data = 2;</code>
       * @param value The data to set.
       * @return This builder for chaining.
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        data_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes data = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }

      private java.lang.Object bucketName_ = "";
      /**
       * <code>optional string bucketName = 3;</code>
       * @return Whether the bucketName field is set.
       */
      public boolean hasBucketName() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional string bucketName = 3;</code>
       * @return The bucketName.
       */
      public java.lang.String getBucketName() {
        java.lang.Object ref = bucketName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            bucketName_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string bucketName = 3;</code>
       * @return The bytes for bucketName.
       */
      public com.google.protobuf.ByteString
          getBucketNameBytes() {
        java.lang.Object ref = bucketName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          bucketName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string bucketName = 3;</code>
       * @param value The bucketName to set.
       * @return This builder for chaining.
       */
      public Builder setBucketName(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        bucketName_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>optional string bucketName = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearBucketName() {
        bucketName_ = getDefaultInstance().getBucketName();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
        return this;
      }
      /**
       * <code>optional string bucketName = 3;</code>
       * @param value The bytes for bucketName to set.
       * @return This builder for chaining.
       */
      public Builder setBucketNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        bucketName_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:FileTransferRequest)
    }

    // @@protoc_insertion_point(class_scope:FileTransferRequest)
    private static final FileTransferRequestOuterClass.FileTransferRequest DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new FileTransferRequestOuterClass.FileTransferRequest();
    }

    public static FileTransferRequestOuterClass.FileTransferRequest getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<FileTransferRequest>
        PARSER = new com.google.protobuf.AbstractParser<FileTransferRequest>() {
      @java.lang.Override
      public FileTransferRequest parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<FileTransferRequest> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<FileTransferRequest> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public FileTransferRequestOuterClass.FileTransferRequest getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_FileTransferRequest_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_FileTransferRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031FileTransferRequest.proto\"I\n\023FileTrans" +
      "ferRequest\022\020\n\010fileName\030\001 \002(\t\022\014\n\004data\030\002 \002" +
      "(\014\022\022\n\nbucketName\030\003 \001(\t"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_FileTransferRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_FileTransferRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_FileTransferRequest_descriptor,
        new java.lang.String[] { "FileName", "Data", "BucketName", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
