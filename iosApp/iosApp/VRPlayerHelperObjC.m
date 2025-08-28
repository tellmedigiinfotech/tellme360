//
//  VRPlayerHelperObjC.m
//  TellMe360
//
//  Objective-C wrapper for VR video player functionality
//

#import "VRPlayerHelper-Bridging-Header.h"
#import "TellMe360-Swift.h"

@implementation VRPlayerHelperObjC

+ (UIViewController* _Nonnull)createVRPlayerWithVideoUrl:(NSString* _Nonnull)videoUrl videoTitle:(NSString* _Nonnull)videoTitle {
    return [VRPlayerHelper createVRPlayerViewControllerWithVideoUrl:videoUrl videoTitle:videoTitle];
}

@end



