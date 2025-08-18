//
//  VRPlayerHelper-Bridging-Header.h
//  TellMe360
//
//  Bridging header for VR video player functionality
//

#ifndef VRPlayerHelper_Bridging_Header_h
#define VRPlayerHelper_Bridging_Header_h

#import <UIKit/UIKit.h>

// Make VRPlayerHelper available to Objective-C and Kotlin/Native
@interface VRPlayerHelperObjC : NSObject

+ (UIViewController* _Nonnull)createVRPlayerWithVideoUrl:(NSString* _Nonnull)videoUrl videoTitle:(NSString* _Nonnull)videoTitle;

@end

#endif /* VRPlayerHelper_Bridging_Header_h */


