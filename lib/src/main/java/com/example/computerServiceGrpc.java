package com.example;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.1)",
    comments = "Source: computer.proto")
public class computerServiceGrpc {

  private computerServiceGrpc() {}

  public static final String SERVICE_NAME = "computerService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ComputerOuterClass.ComputerName,
      ComputerOuterClass.ComputerListResponse> METHOD_GET_COMPUTER_LIST_WITH_NAME =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "computerService", "GetComputerListWithName"),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.ComputerName.getDefaultInstance()),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.ComputerListResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ComputerOuterClass.Empty,
      ComputerOuterClass.ComputerListResponse> METHOD_GET_COMPUTER_LIST =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "computerService", "GetComputerList"),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.Empty.getDefaultInstance()),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.ComputerListResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ComputerOuterClass.Empty,
      ComputerOuterClass.Computer> METHOD_GET_REALTIME_COMPUTER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "computerService", "GetRealtimeComputer"),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.Empty.getDefaultInstance()),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.Computer.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ComputerOuterClass.ComputerName,
      ComputerOuterClass.Computer> METHOD_GET_REALTIME_COMPUTER_WITH_NAME =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "computerService", "GetRealtimeComputerWithName"),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.ComputerName.getDefaultInstance()),
          io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(ComputerOuterClass.Computer.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static computerServiceStub newStub(io.grpc.Channel channel) {
    return new computerServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static computerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new computerServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static computerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new computerServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class computerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getComputerListWithName(ComputerOuterClass.ComputerName request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_COMPUTER_LIST_WITH_NAME, responseObserver);
    }

    /**
     */
    public void getComputerList(ComputerOuterClass.Empty request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_COMPUTER_LIST, responseObserver);
    }

    /**
     * <pre>
     *rpc GetRealList(Empty) returns (RealComputerListResponse) {}
     * </pre>
     */
    public void getRealtimeComputer(ComputerOuterClass.Empty request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.Computer> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_REALTIME_COMPUTER, responseObserver);
    }

    /**
     */
    public void getRealtimeComputerWithName(ComputerOuterClass.ComputerName request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.Computer> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_REALTIME_COMPUTER_WITH_NAME, responseObserver);
    }

    @Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_COMPUTER_LIST_WITH_NAME,
            asyncUnaryCall(
              new MethodHandlers<
                ComputerOuterClass.ComputerName,
                ComputerOuterClass.ComputerListResponse>(
                  this, METHODID_GET_COMPUTER_LIST_WITH_NAME)))
          .addMethod(
            METHOD_GET_COMPUTER_LIST,
            asyncUnaryCall(
              new MethodHandlers<
                ComputerOuterClass.Empty,
                ComputerOuterClass.ComputerListResponse>(
                  this, METHODID_GET_COMPUTER_LIST)))
          .addMethod(
            METHOD_GET_REALTIME_COMPUTER,
            asyncUnaryCall(
              new MethodHandlers<
                ComputerOuterClass.Empty,
                ComputerOuterClass.Computer>(
                  this, METHODID_GET_REALTIME_COMPUTER)))
          .addMethod(
            METHOD_GET_REALTIME_COMPUTER_WITH_NAME,
            asyncUnaryCall(
              new MethodHandlers<
                ComputerOuterClass.ComputerName,
                ComputerOuterClass.Computer>(
                  this, METHODID_GET_REALTIME_COMPUTER_WITH_NAME)))
          .build();
    }
  }

  /**
   */
  public static final class computerServiceStub extends io.grpc.stub.AbstractStub<computerServiceStub> {
    private computerServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private computerServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected computerServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new computerServiceStub(channel, callOptions);
    }

    /**
     */
    public void getComputerListWithName(ComputerOuterClass.ComputerName request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_COMPUTER_LIST_WITH_NAME, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getComputerList(ComputerOuterClass.Empty request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_COMPUTER_LIST, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *rpc GetRealList(Empty) returns (RealComputerListResponse) {}
     * </pre>
     */
    public void getRealtimeComputer(ComputerOuterClass.Empty request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.Computer> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_REALTIME_COMPUTER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getRealtimeComputerWithName(ComputerOuterClass.ComputerName request,
        io.grpc.stub.StreamObserver<ComputerOuterClass.Computer> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_REALTIME_COMPUTER_WITH_NAME, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class computerServiceBlockingStub extends io.grpc.stub.AbstractStub<computerServiceBlockingStub> {
    private computerServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private computerServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected computerServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new computerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public ComputerOuterClass.ComputerListResponse getComputerListWithName(ComputerOuterClass.ComputerName request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_COMPUTER_LIST_WITH_NAME, getCallOptions(), request);
    }

    /**
     */
    public ComputerOuterClass.ComputerListResponse getComputerList(ComputerOuterClass.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_COMPUTER_LIST, getCallOptions(), request);
    }

    /**
     * <pre>
     *rpc GetRealList(Empty) returns (RealComputerListResponse) {}
     * </pre>
     */
    public ComputerOuterClass.Computer getRealtimeComputer(ComputerOuterClass.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_REALTIME_COMPUTER, getCallOptions(), request);
    }

    /**
     */
    public ComputerOuterClass.Computer getRealtimeComputerWithName(ComputerOuterClass.ComputerName request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_REALTIME_COMPUTER_WITH_NAME, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class computerServiceFutureStub extends io.grpc.stub.AbstractStub<computerServiceFutureStub> {
    private computerServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private computerServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected computerServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new computerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ComputerOuterClass.ComputerListResponse> getComputerListWithName(
        ComputerOuterClass.ComputerName request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_COMPUTER_LIST_WITH_NAME, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ComputerOuterClass.ComputerListResponse> getComputerList(
        ComputerOuterClass.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_COMPUTER_LIST, getCallOptions()), request);
    }

    /**
     * <pre>
     *rpc GetRealList(Empty) returns (RealComputerListResponse) {}
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ComputerOuterClass.Computer> getRealtimeComputer(
        ComputerOuterClass.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_REALTIME_COMPUTER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ComputerOuterClass.Computer> getRealtimeComputerWithName(
        ComputerOuterClass.ComputerName request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_REALTIME_COMPUTER_WITH_NAME, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_COMPUTER_LIST_WITH_NAME = 0;
  private static final int METHODID_GET_COMPUTER_LIST = 1;
  private static final int METHODID_GET_REALTIME_COMPUTER = 2;
  private static final int METHODID_GET_REALTIME_COMPUTER_WITH_NAME = 3;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final computerServiceImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(computerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_COMPUTER_LIST_WITH_NAME:
          serviceImpl.getComputerListWithName((ComputerOuterClass.ComputerName) request,
              (io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse>) responseObserver);
          break;
        case METHODID_GET_COMPUTER_LIST:
          serviceImpl.getComputerList((ComputerOuterClass.Empty) request,
              (io.grpc.stub.StreamObserver<ComputerOuterClass.ComputerListResponse>) responseObserver);
          break;
        case METHODID_GET_REALTIME_COMPUTER:
          serviceImpl.getRealtimeComputer((ComputerOuterClass.Empty) request,
              (io.grpc.stub.StreamObserver<ComputerOuterClass.Computer>) responseObserver);
          break;
        case METHODID_GET_REALTIME_COMPUTER_WITH_NAME:
          serviceImpl.getRealtimeComputerWithName((ComputerOuterClass.ComputerName) request,
              (io.grpc.stub.StreamObserver<ComputerOuterClass.Computer>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_GET_COMPUTER_LIST_WITH_NAME,
        METHOD_GET_COMPUTER_LIST,
        METHOD_GET_REALTIME_COMPUTER,
        METHOD_GET_REALTIME_COMPUTER_WITH_NAME);
  }

}
